using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.shared.extentions;
using hotel_api.Services;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public class UserService(
    IConfig config,
    IFileServices fileServices,
    IEmail email,
    IUnitOfWork unitOfWork
)
    : IUserServices
{
    public async Task<Result<AuthDto?>> signup(SignupDto signupDto)
    {
        if (signupDto.Role != 0 && signupDto.Role != 1)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "role must be 1 or 0",
                isSeccessful: false,
                statusCode: 400
            );
        }

        string? validationResult = clsValidation
            .validateInput(
                signupDto.Email,
                signupDto.Password,
                signupDto.Phone
            );

        if (validationResult != null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: validationResult,
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await unitOfWork.UserRepository.isExistByEmail(signupDto.Email))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "email already exist",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await unitOfWork.UserRepository.isExistByPhone(signupDto.Phone))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "phone already exist",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (signupDto.Role == 0 && await unitOfWork.UserRepository.isExist(0))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "you cannot create a user with exist role",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Guid userId = clsUtil.generateGuid();
        User user = new User
        {
            Id = userId,
            Name = signupDto.Name,
            Phone = signupDto.Phone,
            Password = clsUtil.hashingText(signupDto.Password),
            Role = signupDto.Role ?? 1,
            deviceToken = signupDto.DeviceToken ?? "",
            Thumbnail = "",
            CreatedAt = DateTime.Now,
            Email = signupDto.Email,
            UpdatedAt = null,
        };

        unitOfWork.UserRepository.add(user);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "there are error in create new user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        string token = "", refreshToken = "";

        token = AuthinticationUtil.generateToken(
            userId: userId,
            email: signupDto.Email,
            config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: userId,
            email: signupDto.Email,
            config,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 201
        );
    }

    public async Task<Result<AuthDto?>> login(LoginDto loginDto)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(loginDto.Username,
                clsUtil.hashingText(loginDto.Password)
            );

        var isValide = user.isValidateFunc(false);
        if (isValide is not null)
        {
            return new Result<AuthDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        string token = "", refreshToken = "";

        token = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            config,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }


    public async Task<Result<UserInfoDto?>> getMe(Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(id);

        var validate = user.isValidateFunc(false);
        if (validate is not null)
        {
            return new Result<UserInfoDto?>(
                isSeccessful: false,
                data: null,
                message: validate.Message,
                statusCode: validate.StatusCode
            );
        }

        return new Result<UserInfoDto?>(
            isSeccessful: true,
            data: user!.toUserInfoDto(config.getKey("url_file")),
            message: "",
            statusCode: 200
        );
    }


    public async Task<Result<List<UserInfoDto>?>> getUsers(
        int page,
        Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(id);

        var isValide = user.isValidateFunc(false);
        if (isValide is not null)
        {
            return new Result<List<UserInfoDto>?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        List<UserInfoDto> users = (await unitOfWork.UserRepository
                .getUsers(page, 25))
            .Select(u => u.toUserInfoDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<UserInfoDto>?>
        (
            data: users,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> blockOrUnBlockUser(Guid id, Guid userId)
    {
        User? admin = await unitOfWork.UserRepository
            .getUser(id);

        var isValideAdmin = admin.isValidateFunc();
        if (isValideAdmin is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValideAdmin.Message,
                statusCode: isValideAdmin.StatusCode
            );
        }

        User? user = await unitOfWork.UserRepository.getUser(userId);

        isValideAdmin = user.isValidateFunc();

        //this to handle if user that admin want to block is not admin
        if (isValideAdmin?.StatusCode == 404 || isValideAdmin == null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: $"unable to {(user?.IsBlocked == true ? "block" : "unblock")}  user",
                statusCode: isValideAdmin?.StatusCode ?? 400
            );
        }

        user!.IsBlocked = !user.IsBlocked;

        unitOfWork.UserRepository.update(user);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while change user Blocking status",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: false,
            statusCode: 204
        );
    }


    public async Task<Result<UserInfoDto?>> updateUser(
        UpdateUserInfoDto userDto,
        Guid id)
    {
        if (userDto.isEmpty())
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "no data changes",
                isSeccessful: false,
                statusCode: 200
            );


        User? user = await unitOfWork.UserRepository.getUser(id);

        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<UserInfoDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        if (userDto.Phone is not null && user?.Phone != userDto.Phone)
        {
            bool isExistPhone = await unitOfWork.UserRepository.isExistByPhone(userDto.Phone ?? "");

            if (isExistPhone)
            {
                return new Result<UserInfoDto?>
                (
                    data: null,
                    message: "phone already exist",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }

        string? hashedPassword =
            string.IsNullOrEmpty(userDto.Password)
            || string.IsNullOrEmpty(userDto.NewPassword)
                ? null
                : clsUtil.hashingText(userDto.NewPassword);

        if (userDto.Password != null && userDto.NewPassword != null)
        {
            if (user.Password != clsUtil.hashingText(userDto.Password))
            {
                return new Result<UserInfoDto?>
                (
                    data: null,
                    message: "password not corrected",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }

        string? profile = null;
        if (userDto.Thumbnail != null)
        {
            profile = await fileServices.saveFile(userDto.Thumbnail, EnImageType.PROFILE);
        }

        user.Thumbnail = profile ?? user.Thumbnail;
        user.Name = userDto.Name ?? user.Name;
        user.Phone = userDto.Phone ?? user.Phone;
        user.UpdatedAt = DateTime.Now;
        user.Password = hashedPassword ?? user.Password;

        unitOfWork.UserRepository.update(user);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "error while updating user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<UserInfoDto?>
        (
            data: user.toUserInfoDto(config.getKey("url_file")),
            message: "password not corrected",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<AddressDto?>> addAddressToUser(
        CreateAddressDto addressDto,
        Guid id
    )
    {
        User? user = await unitOfWork.UserRepository
            .getUser(id);
        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<AddressDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        int addressCount = await unitOfWork.AddressRepository.getAddressCount(id);

        if (addressCount == 20)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "maximum 20 addresses reached",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Address address = new Address
        {
            Id = clsUtil.generateGuid(),
            Longitude = addressDto.Longitude,
            Latitude = addressDto.Latitude,
            Title = addressDto.Title,
            OwnerId = user!.Id,
            IsCurrent = true
        };

        unitOfWork.AddressRepository.makeAddressNotCurrentToId(user.Id);

        unitOfWork.AddressRepository.add(address);
        var result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "error while adding address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<AddressDto?>
        (
            data: address.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<AddressDto?>> updateUserAddress(
        UpdateAddressDto addressDto,
        Guid id)
    {
        if (addressDto.isEmpty())
            return new Result<AddressDto?>
            (
                data: null,
                message: "nothing to be updated",
                isSeccessful: true,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .getUser(id);
        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<AddressDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        if (
            (addressDto.Longitude is null && addressDto.Latitude is not null) ||
            (addressDto.Longitude is not null && addressDto.Latitude is null)
        )
        {
            return new Result<AddressDto?>(
                isSeccessful: false,
                data: null,
                message: "when update address you must change both longitude and latitude not one of them only ",
                statusCode: 400
            );
        }


        Address? address = await unitOfWork.AddressRepository.getAddress(addressDto.Id);

        if (address is null)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "address not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "address not owned",
                isSeccessful: false,
                statusCode: 400
            );
        }


        address.Longitude = addressDto.Longitude ?? address.Longitude;
        address.Title = addressDto.Title ?? address.Title;
        address.Latitude = addressDto.Latitude ?? address.Latitude;

        unitOfWork.AddressRepository.update(address);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "error while updating address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<AddressDto?>
        (
            data: address.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<bool>> deleteUserAddress(Guid addressId, Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(id);
        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Address? address = await unitOfWork.AddressRepository.getAddress(addressId);

        if (address is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not owned",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (address.IsCurrent)
        {
            return new Result<bool>
            (
                data: false,
                message: "could not delete current address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.AddressRepository.delete(addressId);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }


    public async Task<Result<bool>> updateUserCurrentAddress(Guid addressId, Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(id);
        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Address? address = await unitOfWork.AddressRepository.getAddress(addressId);

        if (address is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not owned",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (address.IsCurrent)
        {
            return new Result<bool>
            (
                data: false,
                message: "address is already current address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.AddressRepository.makeAddressNotCurrentToId(user!.Id);


        unitOfWork.AddressRepository.updateCurrentLocation(addressId, user!.Id);
        var result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update current address",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }


    public async Task<Result<bool>> generateOtp(ForgetPasswordDto forgetPasswordDto)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(forgetPasswordDto.Email);

        var isValide = user.isValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        string otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
        bool isOtpExist = await unitOfWork.PasswordRepository.isExist(otp, user!.Email);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = await unitOfWork.PasswordRepository.isExist(otp, user!.Email);
            } while (isOtpExist);
        }

        unitOfWork.PasswordRepository.add(
            new ReseatePasswordOtp
            {
                Email = forgetPasswordDto.Email,
                CreatedAt = DateTime.Now.AddHours(1),
                Id = clsUtil.generateGuid(),
                Otp = otp
            }
        );
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while generate otp",
                isSeccessful: false,
                statusCode: 400
            );
        }

        var SendMessageSerivce = new SendMessageSerivcies(email);
        bool emailSendResult = await SendMessageSerivce.sendMessage(message: otp, otp);

        if (!emailSendResult)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while send  otp email",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: false,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> otpVerification(CreateVerificationDto otp)
    {
        bool isExistUser = await unitOfWork.UserRepository
            .isExistByEmail(otp.Email);
        if (!isExistUser)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        ReseatePasswordOtp? otpResult = await unitOfWork.PasswordRepository.getOtp(otp.Otp, otp.Email);


        if (otpResult is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "otp not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        otpResult.IsValidated = true;

        unitOfWork.PasswordRepository.update(otpResult);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update otp",
                isSeccessful: false,
                statusCode: 400
            );
        }


        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<AuthDto?>> reseatePassword(CreateReseatePasswordDto otp)
    {
        bool isExistUser = await unitOfWork.UserRepository
            .isExistByEmail(otp.Email);
        if (!isExistUser)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        ReseatePasswordOtp? otpResult = await unitOfWork.PasswordRepository.getOtp(otp.Otp, otp.Email, true);


        if (otpResult is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "otp not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        User? user = await unitOfWork.UserRepository.getUser(otp.Email);

        var isValide = user.isValidateFunc();
        if (isValide is not null)
        {
            return new Result<AuthDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        user.Password = clsUtil.hashingText(otp.Password);

        unitOfWork.UserRepository.update(user);
        int result = await unitOfWork.saveChanges();
        
        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while update user password",
                isSeccessful: false,
                statusCode: 400
            );
        }


        string token = "", refreshToken = "";

        token = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            config,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }
}