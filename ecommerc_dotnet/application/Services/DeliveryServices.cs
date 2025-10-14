using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public enum enBelongToType
{
    ADMIN,
    STORE
};

public class DeliveryServices(
    IConfig config,
    IWebHostEnvironment host,
    IUnitOfWork unitOfWork,
    IFileServices fileServices,
    IUserServices userServices,
    IAuthenticationService authenticationService
)
    : IDeliveryServices
{
    public async Task<Result<AuthDto?>> login(LoginDto loginDto)
    {
        if (string.IsNullOrWhiteSpace(loginDto.DeviceToken))
            return new Result<AuthDto?>
            (
                data: null,
                message: "you should login from phone",
                isSeccessful: false,
                statusCode: 400
            );

        User? user = await unitOfWork.UserRepository
            .getUser(
                loginDto.Username,
                clsUtil.hashingText(loginDto.Password));

        var isValide = user.isValidateFunc(isAdmin: false);

        if (isValide is not null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: isValide.Message,
                isSeccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        Delivery? delivery = await unitOfWork.DeliveryRepository.getDeliveryByUserId(user.Id);

        if (delivery is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "delivery not found",
                isSeccessful: false,
                statusCode: 404
            );
        }


        if (delivery.IsBlocked)
            return new Result<AuthDto?>
            (
                data: null,
                message: "delivery is blocked",
                isSeccessful: false,
                statusCode: 404
            );
        delivery.DeviceToken = loginDto.DeviceToken;

        unitOfWork.DeliveryRepository.update(delivery);
        int result = await unitOfWork.saveChanges();
        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSeccessful: false,
                statusCode: 400
            );
        }


        string? token = null, refreshToken = null;
        token = authenticationService.generateToken(
            id: delivery.Id,
            email: delivery.User.Email);

        refreshToken = authenticationService.generateToken(
            id: delivery.Id,
            email: delivery.User.Email,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }

    public async Task<Result<DeliveryDto?>> createDelivery(
        Guid userId,
        CreateDeliveryDto deliveryDto
    )
    {
        User? user = await unitOfWork.UserRepository
            .getUser(userId);


        var admin = user.isValidateFunc(isAdmin: true);
        var store = user.isValidateFunc(isAdmin: false, isStore: true);


        if (admin is not null && user.Role == 0 || store != null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: admin?.Message ?? store.Message,
                isSeccessful: false,
                statusCode: admin?.StatusCode ?? store.StatusCode
            );
        }


        if (await unitOfWork.DeliveryRepository.isExistByUserId(deliveryDto.UserId))
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery already exists",
                isSeccessful: false,
                statusCode: 400
            );
        }


        string? deliveryThumnail = null;
        if (deliveryDto.Thumbnail is not null)
        {
            deliveryThumnail = await fileServices 
                .saveFile(
                    deliveryDto.Thumbnail,
                    EnImageType.DELIVERY);
        }


        var addressId = clsUtil.generateGuid();
        var id = clsUtil.generateGuid();
        Address address = new Address
        {
            Id = addressId,
            Title = "my Place",
            CreatedAt = DateTime.Now,
            OwnerId = id
        };

        Delivery delivery = new Delivery
        {
            Id = id,
            CreatedAt = DateTime.Now,
            UserId = deliveryDto.UserId,
            Thumbnail = deliveryThumnail,
            Address = address,
            BelongTo = user?.Store?.Id ?? userId
        };

        unitOfWork.DeliveryRepository.add(delivery);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSeccessful: false,
                statusCode: 400
            );
        }

        delivery = await unitOfWork.DeliveryRepository.getDelivery(id);

        return new Result<DeliveryDto?>
        (
            data: delivery?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<DeliveryDto?>> updateDeliveryStatus(Guid id, bool status)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .getDelivery(id);
        if (delivery is null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (delivery.IsBlocked)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery is blocked",
                isSeccessful: false,
                statusCode: 404
            );
        }

        delivery.IsBlocked = status;

        unitOfWork.DeliveryRepository.update(delivery);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "error while update delivery",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<DeliveryDto?>
        (
            data: delivery?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<DeliveryDto?>> getDelivery(Guid id)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .getDelivery(id);

        if (delivery is null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (delivery.IsBlocked)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery is blocked",
                isSeccessful: false,
                statusCode: 404
            );
        }

        var deliveryDto = delivery.toDto(config.getKey("url_file"));
        deliveryDto.Analys = await unitOfWork.DeliveryRepository.getDeliveryAnalys(delivery.Id);

        return new Result<DeliveryDto?>
        (
            data: deliveryDto,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<DeliveryDto>>> getDeliveries(
        Guid belongToId,
        int pageNumber,
        int pageSize
    )
    {
        User? user = await unitOfWork.UserRepository
            .getUser(belongToId);

        enBelongToType belongType = enBelongToType.ADMIN;

        switch (user.Role == 0)
        {
            case true:
            {
                belongType = enBelongToType.ADMIN;
            }
                break;
            default:
            {
                belongType = enBelongToType.STORE;
            }
                break;
        }

        Guid id = Guid.NewGuid();
        switch (belongType)
        {
            case enBelongToType.STORE:
            {
                var isValidated = user.isValidateFunc(isStore: true);
                if (isValidated is not null)
                {
                    return new Result<List<DeliveryDto>>
                    (
                        data: new List<DeliveryDto>(),
                        message: isValidated.Message,
                        isSeccessful: false,
                        statusCode: isValidated.StatusCode
                    );
                }

                id = user.Store.Id;
            }
                break;
            case enBelongToType.ADMIN:
            {
                var isValidated = user.isValidateFunc();
                if (isValidated is not null)
                {
                    return new Result<List<DeliveryDto>>
                    (
                        data: new List<DeliveryDto>(),
                        message: isValidated.Message,
                        isSeccessful: false,
                        statusCode: isValidated.StatusCode
                    );
                }

                id = user.Id;
            }
                break;
        }


        List<DeliveryDto>? deliveryDto = (await unitOfWork.DeliveryRepository
                .getDeliveryByBelongTo(id, pageNumber, pageSize))
            ?.Select((de) => de.toDto(config.getKey("url_file")))
            ?.ToList();

        if (deliveryDto is not null)
            foreach (var delivery in deliveryDto)
            {
                delivery.Analys = await unitOfWork.DeliveryRepository.getDeliveryAnalys(delivery.Id);
            }

        return new Result<List<DeliveryDto>>
        (
            data: deliveryDto,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<DeliveryDto>> updateDelivery(UpdateDeliveryDto deliveryDto, Guid id)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .getDelivery(id);

        var isValidated = delivery.isValidated();

        if (isValidated is not null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: isValidated.Message,
                isSeccessful: false,
                statusCode: isValidated.StatusCode
            );
        }

        bool isPassOperation = false;

        if (deliveryDto.Longitude is not null && deliveryDto.Latitude is not null)
        {
            var addressHolder = delivery.Address;
            addressHolder.Longitude = deliveryDto.Longitude;
            addressHolder.Latitude = deliveryDto.Latitude;
            addressHolder.IsCurrent = true;

            unitOfWork.AddressRepository.update(addressHolder);
        }

        if (!isPassOperation)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "could not update delivery address",
                isSeccessful: false,
                statusCode: 404
            );
        }

        var userUpdateData = new UpdateUserInfoDto
        {
            Name = deliveryDto.Name,
            Phone = deliveryDto.Phone,
            Thumbnail = deliveryDto.Thumbnail,
            Password = deliveryDto.Password,
            NewPassword = deliveryDto.NewPassword,
        };


        if (deliveryDto.Thumbnail is not null)
        {
            var previuse = delivery.Thumbnail;
            if (previuse is not null)
                fileServices.deleteFile(filePath: previuse);

            string? newThumbNail = null;
            newThumbNail = await fileServices.saveFile(file: deliveryDto.Thumbnail, type: EnImageType.DELIVERY);
            delivery.Thumbnail = newThumbNail;

            unitOfWork.DeliveryRepository.update(delivery);
             
        }

        var result = await unitOfWork.saveChanges();
        
        if (userUpdateData.isUpdateAnyFeild() is true)
        {
            var updateUserData = await userServices.updateUser(userUpdateData, delivery!.UserId);

            if (updateUserData.Data is not null)
            {
                return new Result<DeliveryDto>
                (
                    data: null,
                    message: updateUserData.Message,
                    isSeccessful: false,
                    statusCode: updateUserData.StatusCode
                );
            }
        }
        
        if (result <1)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "Something went wrong",
                isSeccessful: false,
                statusCode: 404
            );
        }
        

        delivery = await unitOfWork.DeliveryRepository.getDelivery(delivery.Id);

        if (delivery is null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "Delivery not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        return new Result<DeliveryDto>
        (
            data: delivery?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }
}