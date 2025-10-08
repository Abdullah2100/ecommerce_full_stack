using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface IUserServices
{
    public Task<Result<AuthDto?>> signup(SignupDto signupDto);
    public Task<Result<AuthDto?>> login(LoginDto loginDto);


    public Task<Result<UserInfoDto?>> getMe(Guid id);

    public Task<Result<List<UserInfoDto>?>> getUsers(int page, Guid id);

    public Task<Result<bool>> blockOrUnBlockUser(Guid id,Guid userId);
    
    public Task<Result<UserInfoDto?>> updateUser(UpdateUserInfoDto userDto, Guid id);

    public Task<Result<AddressDto?>> addAddressToUser(CreateAddressDto addressDto, Guid id);
    public Task<Result<AddressDto?>> updateUserAddress(UpdateAddressDto addressDto, Guid id);
    public Task<Result<bool>> deleteUserAddress(Guid addressId, Guid id);
    public Task<Result<bool>> updateUserCurrentAddress(Guid addressId, Guid id);

    public Task<Result<bool>> generateOtp(ForgetPasswordDto forgetPasswordDto);
    public Task<Result<bool>> otpVerification(CreateVerificationDto createVerificationDto);
    public Task<Result<AuthDto?>> reseatePassword(CreateReseatePasswordDto createReseatePasswordDto);
}