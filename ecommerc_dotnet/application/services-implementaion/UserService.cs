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
        if (user is null)
        {
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        return new Result<UserInfoDto?>(
            isSeccessful: true,
            data: user.toUserInfoDto(_config.getKey("url_file")),
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
        if (user is null)
        {
            return new Result<List<UserInfoDto>?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role == 1)
        {
            return new Result<List<UserInfoDto>?>
            (
                data: null,
                message: "you not have the permission",
                isSeccessful: false,
                statusCode: 400
            );
        }


        var users = await _userRepository
            .getAllAsync(page, 25);

        List<UserInfoDto>? usersInfo = users?
            .Select(u => u.toUserInfoDto(_config.getKey("url_file")))
            .ToList();

        return new Result<List<UserInfoDto>?>
        (
            data: usersInfo ?? new List<UserInfoDto>(),
            message: "you not have the permission",
            isSeccessful: false,
            statusCode: 400
        );
    }

    public async Task<Result<bool>> blockOrUnBlockUser(Guid id, Guid userId)
    {
        User? admin = await _userRepository
            .getUser(id);
        if (admin is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (admin.Role == 1)
        {
            return new Result<bool>
            (
                data: false,
                message: "you not have the permission",
                isSeccessful: false,
                statusCode: 400
            );
        }

        User? user = await _userRepository.getUser(userId);

        if (user is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user thant want to blocked not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        user.IsBlocked = !user.IsBlocked;
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

    public async Task<Result<int>> getUserCount(Guid id)
    {
        User? user = await _userRepository
            .getUser(id);
        if (user is null)
        {
            return new Result<int>
            (
                data: 0,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role == 1)
        {
            return new Result<int>
            (
                data: 0,
                message: "you not have the permission",
                isSeccessful: false,
                statusCode: 400
            );
        }

        user.IsBlocked = !user.IsBlocked;
        int result = await _userRepository.getUserCount();


        return new Result<int>
        (
            data: result,
            message: "",
            isSeccessful: true,
            statusCode: 200
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
        if (user is null)
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );

        if (id != user.Id)
        {
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "only own user data can change her data ",
                isSeccessful: false,
                statusCode: 400
            );
        }

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
        if (user is null)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "user is blocked",
                isSeccessful: false,
                statusCode: 400
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
            OwnerId = user.Id
        };

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
        User? user = await _userRepository
            .getUser(id);
        if (user is null)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (addressDto.isEmpty())
            return new Result<AddressDto?>
            (
                data: null,
                message: "nothing to be updated",
                isSeccessful: true,
                statusCode: 200
            );

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
        if (user is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
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
            statusCode:204 
        );
    }


    public async Task<Result<bool>> updateUserCurrentAddress(Guid addressId, Guid id)
    {
        User? user = await _userRepository
            .getUser(id);
        if (user is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
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

        int result = await _addressRepository.updateCurrentLocation(addressId, user.Id);

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
        bool isExistUser = await _userRepository
            .isExistByEmail(forgetPasswordDto.Email);
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

        string otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
        bool isOtpExist = await _passwordRepository.isExist(otp);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = await _passwordRepository.isExist(otp);
            } while (isOtpExist);
        }

        int result = await _passwordRepository.addAsync(
            new ReseatePasswordOtp
            {
                Email = forgetPasswordDto.Email,
                CreatedAt = DateTime.Now,
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

        ReseatePasswordOtp? otpResult = await _passwordRepository.getOtp(otp.Otp, otp.Email);


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