using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace ecommerc_dotnet.infrastructure.services;

public class DeliveryServices(
    IUserRepository userRepository,
    IDeliveryRepository deliveryRepository,
    IConfig config,
    IWebHostEnvironment host)
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

        User? user = await userRepository
            .getUser(
                loginDto.Username,
                clsUtil.hashingText(loginDto.Password));
        if (user is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        Delivery? delivery = await deliveryRepository.getDeliveryByUserId(user.Id);

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

        int result = await deliveryRepository.updateAsync(delivery);
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
        token = AuthinticationUtil.generateToken(
            userId: delivery.UserId,
            email: delivery.User.Email,
            config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: delivery.UserId,
            email: delivery.User.Email,
            config,
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
        User? user = await userRepository
            .getUser(userId);


        var admin = user.isValidateFunc(isAdmin: true);
        var store = user.isValidateFunc(isAdmin:false,isStore:true);
       
        
        if (admin is not null && user.Role==0||store!=null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: admin?.Message ?? store.Message,
                isSeccessful: false,
                statusCode:admin?.StatusCode?? store.StatusCode
            );
        }

        
        
        if (await deliveryRepository.isExistByUserId(deliveryDto.UserId))
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
            deliveryThumnail = await clsUtil
                .saveFile(
                    deliveryDto.Thumbnail,
                    EnImageType.DELIVERY, host);
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
            BelongTo = user?.Store?.Id??userId
        };
        int result = await deliveryRepository.addAsync(delivery);

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

        delivery = await deliveryRepository.getDelivery(id);

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
        Delivery? delivery = await deliveryRepository
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
        int result = await deliveryRepository.updateAsync(delivery);

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

    public async Task<Result<DeliveryDto?>> getDelivery(Guid userId)
    {
        Delivery? delivery = await deliveryRepository
            .getDeliveryByUserId(userId);
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
        deliveryDto.Analys = await deliveryRepository.getDeliveryAnalys(delivery.Id);

        return new Result<DeliveryDto?>
        (
            data: deliveryDto,
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<List<DeliveryDto>>> getDeliveries(Guid adminId, int pageNumber, int pageSize)
    {
        User? user = await userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<List<DeliveryDto>>
            (
                data: new List<DeliveryDto>(),
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<List<DeliveryDto>>
            (
                data: new List<DeliveryDto>(),
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        List<DeliveryDto> deliveryDtos = (await deliveryRepository
                .getAllAsync(pageNumber, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();
        foreach (var deliveryDto in deliveryDtos)
        {
            deliveryDto.Analys = await deliveryRepository.getDeliveryAnalys(deliveryDto.Id);
        }

        return new Result<List<DeliveryDto>>
        (
            data: deliveryDtos,
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }
}