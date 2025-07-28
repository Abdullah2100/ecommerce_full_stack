using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.Services.EmailService;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/User")]
public class UserController : ControllerBase
{
    public UserController(
        AppDbContext dbContext,
        IConfig configuration,
        IWebHostEnvironment webHostEnvironment,
        IEmail email,
        IUnitOfWork unitOfWork
    )
    {
        _configuration = configuration;
        _userService = new UserService(dbContext, configuration, unitOfWork);
        _host = webHostEnvironment;
        _addressData = new AddressData(unitOfWork, dbContext);
        _forgetPasswordData = new ForgetPasswordData(dbContext, unitOfWork);
        _email = email;
    }

    private readonly IConfig _configuration;
    private readonly IEmail _email;

    private readonly UserService _userService;

    private readonly ForgetPasswordData _forgetPasswordData;
    private readonly AddressData _addressData;
    private readonly IWebHostEnvironment _host;

    [AllowAnonymous]
    [HttpPost("signup")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> signUp([FromBody] SignupDto data)
    {
        if (data.Role != 0 && data.Role != 1)
        {
            return BadRequest("role must be 1 or 0");
        }

        string? validationResult = clsValidation.validateInput(data.Email, data.Password, data.Phone);

        if (validationResult != null)
        {
            return BadRequest(validationResult);
        }

        if (await _userService.isExistByEmail(data.Email))
        {
            return BadRequest("email already exist");
        }

        if (await _userService.isExistByPhone(data.Phone))
        {
            return BadRequest("phone already exist");
        }

        UserInfoDto? result = await _userService.createNew(
            name: data.Name,
            email: data.Email,
            phone: data.Phone,
            password: clsUtil.hashingText(data.Password),
            role: data.Role,
            deviceToken: data.DeviceToken
        );
        if (result is null)
            return BadRequest("هناك مشكلة ما");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration,
            EnTokenMode.RefreshToken);

        return StatusCode(StatusCodes.Status201Created
            , new { token = token, refreshToken = refreshToken });
    }


    
    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> login([FromBody] LoginDto data)
    {
        UserInfoDto? result = await _userService.getUser(data.Username, clsUtil.hashingText(data.Password));
        if (result is null)
            return BadRequest("المستخدم غير موجود");

        await _userService.updateUserDeviceToken(id: result.Id, deviceToken: data.DeviceToken);

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration,
            EnTokenMode.RefreshToken);

        return StatusCode(200
            , new { token = token, refreshToken = refreshToken });
    }


    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUser()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return BadRequest("المستخدم غير موجود");
        }


        return StatusCode(200, user);
    }


    [HttpGet("{page:int}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUsers(int page)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (user.Role is 1)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        if (page < 0)
        {
            return BadRequest("لا بد من ان تكون الصفحة اكبر من الصفر");
        }

        List<UserInfoDto>? users = await _userService.getUsers(page);

        if (users is null)
            return NoContent();

        return StatusCode(200, users);
    }


    [HttpDelete("{userId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteOrUndeletedUser(Guid userId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (user.Role is 1)
            return BadRequest("ليس لديك الصلاحية ");


        bool? result = await _userService.deleteUser(userId);

        if (result is null or false)
            return BadRequest("حدثة مشكلة اثناء حذف المستخدم");
        return NoContent();
    }


    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUsersPagesSize()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? userData = await _userService.getUser(idHolder.Value);

        if (userData is null || userData.Role != 0)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        int size = await _userService.getUsersLenght();
        int userPages = Convert.ToInt32(Math.Ceiling((double)size / 24));

        return StatusCode(200, userPages);
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateUser(
        [FromForm] UpdateUserInfoDto userData
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        if (userData.isEmpty()) return Ok(" ليس هناك اي شئ يحتاجل للتغيير ");

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (idHolder != user.Id)
        {
            return BadRequest("فقط المستخدم يمكن تعديل بياناته");
        }


        if (userData.Password != null && userData.NewPassword != null)
        {
            if (user.Password != clsUtil.hashingText(userData.Password))
            {
                return BadRequest("كلمة المرور غير صحيحة");
            }
        }

        if (user.Thumbnail != null && userData.Thumbnail != null)
        {
            clsUtil.deleteFile(user.Thumbnail, _host);
        }

        string? profile = null;
        if (userData.Thumbnail != null)
        {
            profile = await clsUtil.saveFile(userData.Thumbnail, EnImageType.PROFILE, _host);
        }

        UserInfoDto? result = await _userService.updateUser(
            id: idHolder.Value,
            phone: userData.Phone,
            password: userData.Password is null ? null : clsUtil.hashingText(userData.Password),
            name: userData.Name,
            profile);

        if (result is null)
            return BadRequest("هناك مشكلة في تحديث البيانات");


        return Ok(result);
    }


    [HttpPost("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> setUserLocation(
        [FromBody] CreateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null || user.IsBlocked)
        {
            return NotFound("المستخدم غير موجود");
        }

        int userLocationCount = await _addressData.getAddressCountForUser(idHolder.Value);
        if (userLocationCount > 10)
            return BadRequest("اقصى حد للاماكن التي يمكن للمستخدم ادخالها هي 10");

        AddressDto? location = await _addressData.addUserAddress(
            title: address.Title,
            longitude: address.Longitude,
            latitude: address.Latitude,
            userId: idHolder.Value
        );
        if (location is null)
            return BadRequest("حدثة مشكلة اثناء حفظ المان");

        return StatusCode(201, location);
    }

    [HttpPut("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateUserLocation(
        [FromBody] UpdateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        if (address.isEmpty()) return Ok("ليس هناك اي شئ يحتاج للتغيير");

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null || user.IsBlocked)
        {
            return NotFound("المستخدم غير موجود");
        }

        Address? addressResult = await _addressData.getAddressById(address.Id);

        if (addressResult is null)
        {
            return NotFound("العنوان غير موجود");
        }

        if (addressResult.OwnerId != idHolder.Value)
        {
            return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
        }

        AddressDto? location = await _addressData.updateAddress(
            id: addressResult.Id,
            titile: address.Title,
            longitude: address.Longitude,
            latitude: address.Latitude
        );

        if (location is null)
            return BadRequest("حدثة مشكلة اثناء تعديل البيانات");

        return StatusCode(200, location);
    }

    [HttpDelete("address/{addressId}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteUserLocation(
        Guid addressId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null || user.IsBlocked)
        {
            return NotFound("المستخدم غير موجود");
        }

        Address? addressResult = await _addressData.getAddressById(addressId);

        if (addressResult is null)
        {
            return NotFound("العنوان غير موجود");
        }

        if (addressResult.OwnerId != idHolder.Value)
        {
            return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
        }

        if (addressResult.IsCurrent)
            return BadRequest("لا يمكن حذف العنوان الحالي");

        bool result = await _addressData.deleteAddress(
            id: addressResult.Id);

        if (result == false)
            return BadRequest("حدثة مشكلة اثناء حذف البيانات");

        return NoContent();
    }


    [HttpPost("address/active{addressId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateUserCurrentLocation(Guid addressId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null || user.IsBlocked)
        {
            return BadRequest("المستخدم غير موجود");
        }

        Address? address = await _addressData.getAddressById(addressId);

        if (address is null)
        {
            return BadRequest("العنوان غير موجود");
        }

        bool? result = await _addressData.updateCurrentAddress(address.Id, user.Id);

        return StatusCode(200, result);
    }

    [AllowAnonymous]
    [HttpPost("generateOtp")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> generateOtp(
        [FromBody] ForgetPasswordDto email
        )
    {
        UserInfoDto? user = await _userService.getUser(email.Email);

        if (user is null)
        {
            NotFound("user not exist");
        }

        string otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");

        bool isOtpExist = await _forgetPasswordData.isExist(otp);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = await _forgetPasswordData.isExist(otp);
            } while (isOtpExist);
        }

        bool result = await _forgetPasswordData.createNewOtp(otp, email.Email);
        bool emailSendResult = await _email.sendingEmail(email.Email, otp);

        if (emailSendResult == false || result == false)
        {
            BadRequest("user not exist");
        }

        return NoContent();
    }


    [AllowAnonymous]
    [HttpPost("otpVerification")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> verifingOtp([FromBody] CreateVerificationDto verification)
    {
        bool result = await _forgetPasswordData.isExist(verification.Email, verification.Otp);
        if (!result)
        {
            return NotFound("not found otp");
        }

        result = await _forgetPasswordData.updateOtpStatus(verification.Otp);

        if (!result)
            return BadRequest("حدثة مشكلة اثناء حفظ البيانات");

        return NoContent();
    }


    [AllowAnonymous]
    [HttpPost("reseatPassword")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> reseatPassword([FromBody] CreateReseatePasswordDto data)
    {
        bool otpValidationResult = await _forgetPasswordData.isExist(data.Email, data.Otp, true);

        if (!otpValidationResult)
        {
            return NotFound("not found otp");
        }

        bool isExist = await _userService.isExistByEmail(data.Email);
        if (!isExist)
            return NotFound("المستخدم غير موجود");
        UserInfoDto? result = await _userService.updateUserPassword(data.Email,
            clsUtil.hashingText(data.Password));
        if (result is null)
            return BadRequest("حدثة مشكلة اثناء حفظ البيانات");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userId: result.Id,
            email: result.Email,
            _configuration,
            EnTokenMode.RefreshToken);

        return StatusCode(200
            , new { token = token, refreshToken = refreshToken });
    }
}