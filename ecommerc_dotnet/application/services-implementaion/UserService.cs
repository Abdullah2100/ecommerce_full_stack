using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.Services;
using hotel_api.util;

namespace ecommerc_dotnet.data;

public class UserService : IUserServices
{
    private readonly IConfig _config;
    private readonly IUserRepository _userRepository;
    private readonly IAddressRepository _addressRepository;
    private readonly IReseatePasswordRepository _passwordRepository;
    private readonly IWebHostEnvironment _host;
    private readonly IEmail _email;


    public UserService(
        IConfig config,
        IWebHostEnvironment host,
        IEmail email,
        IUserRepository userRepository,
        IAddressRepository addressRepository,
        IReseatePasswordRepository passwordRepository
    )
    {
        _config = config;
        _host = host;
        _email = email;
        _userRepository = userRepository;
        _addressRepository = addressRepository;
        _passwordRepository = passwordRepository;
    }


    private static Result<AuthDto?>? isValideFunc(User? user, bool isAdmin = true)
    {
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

        switch (!isAdmin)
        {
            case true:
            {
                if (user.IsBlocked)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user is blocked",
                        isSeccessful: false,
                        statusCode: 404
                    );
                }

                return null;
            }
            default:
            {
                if (user.Role == 1)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user not havs the permission",
                        isSeccessful: false,
                        statusCode: 400
                    );
                }

                return null;
            }
        }
    }

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

        if (await _userRepository.isExistByEmail(signupDto.Email))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "email already exist",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await _userRepository.isExistByPhone(signupDto.Phone))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "phone already exist",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await _userRepository.isExist(0))
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

        int result = await _userRepository.addAsync(user);

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
            _config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: userId,
            email: signupDto.Email,
            _config,
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
        User? user = await _userRepository
            .getUser(loginDto.Username,
                clsUtil.hashingText(loginDto.Password)
            );

        var isValide = isValideFunc(user, false);
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
            _config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            _config,
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
        User? user = await _userRepository
            .getUser(id);

        var isValide = isValideFunc(user, false);

        if (isValide is not null)
        {
            return new Result<UserInfoDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        return new Result<UserInfoDto?>(
            isSeccessful: true,
            data: user!.toUserInfoDto(_config.getKey("url_file")),
            message: "",
            statusCode: 200
        );
    }


    public async Task<Result<List<UserInfoDto>?>> getUsers(
        int page,
        Guid id)
    {
        User? user = await _userRepository
            .getUser(id);

        var isValide = isValideFunc(user);

        if (isValide is not null)
        {
            return new Result<List<UserInfoDto>?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        List<UserInfoDto> users = (await _userRepository
                .getAllAsync(page, 25))
            .Select(u => u.toUserInfoDto(_config.getKey("url_file")))
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
        User? admin = await _userRepository
            .getUser(id);

        var isValideAdmin = isValideFunc(admin);

        if (isValideAdmin is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValideAdmin.Message,
                statusCode: isValideAdmin.StatusCode
            );
        }

        User? user = await _userRepository.getUser(userId);

        isValideAdmin = isValideFunc(user);

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
        int result = await _userRepository.updateAsync(user);

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


        User? user = await _userRepository.getUser(id);

        var isValide = isValideFunc(user, false);

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
            bool isExistPhone = await _userRepository.isExistByPhone(userDto.Phone ?? "");

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
            profile = await clsUtil.saveFile(userDto.Thumbnail, EnImageType.PROFILE, _host);
        }

        user.Thumbnail = profile ?? user.Thumbnail;
        user.Name = userDto.Name ?? user.Name;
        user.Phone = userDto.Phone ?? user.Phone;
        user.UpdatedAt = DateTime.Now;
        user.Password = hashedPassword ?? user.Password;
        int result = await _userRepository.updateAsync(user);
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
            data: user.toUserInfoDto(_config.getKey("url_file")),
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
        User? user = await _userRepository
            .getUser(id);
        var isValide = isValideFunc(user, false);

        if (isValide is not null)
        {
            return new Result<AddressDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        int addressCount = await _addressRepository.getAddressCount(id);

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
        };

        if (addressCount == 0)
        {
            address.IsCurrent =  true;
        }

        int result = await _addressRepository.addAsync(address);

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

        User? user = await _userRepository
            .getUser(id);
        var isValide = isValideFunc(user, false);

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


        Address? address = await _addressRepository.getAddress(addressDto.Id);

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

        int result = await _addressRepository.updateAsync(address);
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
        User? user = await _userRepository
            .getUser(id);
        var isValide = isValideFunc(user, false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        } 

        Address? address = await _addressRepository.getAddress(addressId);

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

        int result = await _addressRepository.deleteAsync(addressId);

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
        User? user = await _userRepository
            .getUser(id);
        var isValide = isValideFunc(user, false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }  

        Address? address = await _addressRepository.getAddress(addressId);

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

        int result = await _addressRepository.updateCurrentLocation(addressId, user!.Id);

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
        User? user = await _userRepository
            .getUser(forgetPasswordDto.Email);
        
        var isValide = isValideFunc(user, false);

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
        bool isOtpExist = await _passwordRepository.isExist(otp,user!.Email);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = await _passwordRepository.isExist(otp,user!.Email);
            } while (isOtpExist);
        }

        int result = await _passwordRepository.addAsync(
            new ReseatePasswordOtp
            {
                Email = forgetPasswordDto.Email,
                CreatedAt = DateTime.Now.AddHours(1),
                Id = clsUtil.generateGuid(),
                Otp = otp
            }
        );
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

        bool emailSendResult = await _email.sendingEmail(forgetPasswordDto.Email, otp);

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
        bool isExistUser = await _userRepository
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

        ReseatePasswordOtp? otpResult = await _passwordRepository.getOtp(otp.Otp, otp.Email);


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
        int result = await _passwordRepository.updateAsync(otpResult);
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
        bool isExistUser = await _userRepository
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

        ReseatePasswordOtp? otpResult = await _passwordRepository.getOtp(otp.Otp, otp.Email,true);


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

        User? user = await _userRepository.getUser(otp.Email);
        
        var isValide = isValideFunc(user);
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

        int result = await _userRepository.updateAsync(user);
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
            _config);

        refreshToken = AuthinticationUtil.generateToken(
            userId: user.Id,
            email: user.Email,
            _config,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }
}