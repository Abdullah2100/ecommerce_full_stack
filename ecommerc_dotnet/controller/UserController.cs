using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
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
        iEmailService emailService
    )
    {
        _configuration = configuration;
        _userData = new UserData(dbContext, configuration);
        _host = webHostEnvironment;
        _addressData = new AddressData(dbContext);
        _forgetPasswordData = new ForgetPasswordData(dbContext);
        _emailService = emailService;
    }

    private readonly IConfig _configuration;
    private readonly iEmailService _emailService;

    private readonly UserData _userData;

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
        if (data.role != 0 && data.role != 1)
        {
            return BadRequest("role must be 1 or 0");
        }

        string? validationResult = clsValidation.validateInput(data.email, data.password, data.phone);

        if (validationResult != null)
        {
            return BadRequest(validationResult);
        }

        if (await _userData.isExistByEmail(data.email))
        {
            //return BadRequest("email already exist");
            return BadRequest("email already exist");
        }

        if (await _userData.isExistByPhone(data.phone))
        {
            return BadRequest("phone already exist");
        }

        UserInfoResponseDto? result = await _userData.createNew(
            name: data.name,
            email: data.email,
            phone: data.phone,
            password: clsUtil.hashingText(data.password),
            role: data.role,
            deviceToken: data.deviceToken
        );
        if (result == null)
            return BadRequest("هناك مشكلة ما");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(StatusCodes.Status201Created
            , new { token = token, refreshToken = refreshToken });
    }


    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> signIn([FromBody] LoginDto data)
    {
        UserInfoResponseDto? result = await _userData.getUser(data.username, clsUtil.hashingText(data.password));
        if (result == null)
            return BadRequest("المستخدم غير موجود");

        await _userData.updateUserDeviceToken(userId: result.Id, deviceToken: data.deviceToken);

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

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
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        UserInfoResponseDto? user = await _userData.getUser(idHolder.Value);

        if (user == null)
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
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (user.role == 1)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        if (page < 0)
        {
            return BadRequest("لا بد من ان تكون الصفحة اكبر من الصفر");
        }

        List<UserInfoResponseDto>? users = await _userData.getUsers(page);

        if (users == null)
            return NoContent();

        return StatusCode(200, users);
    }

    [HttpDelete("{user_id:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteOrUndeletedUser(Guid user_id)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (user.role == 1)
            return BadRequest("ليس لديك الصلاحية ");


        bool result = await _userData.deleteUser(user_id);

        if (result == false)
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
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? userData = await _userData.getUserById(idHolder.Value);

        if (userData == null || userData.role != 0)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        int size = await _userData.getUsers();
        int userPages = Convert.ToInt32(Math.Ceiling((double)size / 24));

        return StatusCode(200, userPages);
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateUser(
        [FromForm] UpdateUserInfo userData
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (idHolder != user.ID)
        {
            return BadRequest("فقط المستخدم يمكن تعديل بياناته");
        }


        if (userData.password != null && userData.newPassword != null)
        {
            if (user.password != clsUtil.hashingText(userData.password))
            {
                return BadRequest("كلمة المرور غير صحيحة");
            }
        }

        if (user.thumbnail != null && userData.thumbnail != null)
        {
            clsUtil.deleteFile(user.thumbnail, _host);
        }

        string? profile = null;
        if (userData.thumbnail != null)
        {
            profile = await clsUtil.saveFile(userData.thumbnail, clsUtil.enImageType.PROFILE, _host);
        }

        UserInfoResponseDto? result = await _userData.updateUser(
            userId: idHolder.Value,
            phone: userData.phone,
            password: userData.password == null ? null : clsUtil.hashingText(userData.password),
            name: userData.name,
            profile);

        if (result == null)
            return BadRequest("هناك مشكلة في تحديث البيانات");


        return Ok(result);
    }


 
    [HttpGet("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getUserLocations()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return NotFound("المستخدم غير موجود");
        }


        List<AddressResponseDto>? locations = await _addressData.getUserAddressByUserId(user.ID);

        if (locations == null)
            return NoContent();

        return StatusCode(200, locations);
    }


    [HttpPost("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> setUserLocation(
        [FromBody] AddressRequestDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return NotFound("المستخدم غير موجود");
        }

        int userLocationCount = await _addressData.addressCountForUser(idHolder.Value);
        if (userLocationCount > 10)
            return BadRequest("اقصى حد للاماكن التي يمكن للمستخدم ادخالها هي 10");

        AddressResponseDto? location = await _addressData.addUserLocation(
            title: address.title,
            longitude: address.longitude,
            latitude: address.latitude,
            userId: idHolder.Value
        );
        if (location == null)
            return BadRequest("حدثة مشكلة اثناء حفظ المان");

        return StatusCode(201, location);
    }

    [HttpPut("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateUserLocation(
        [FromBody] AddressRequestUpdateDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return NotFound("المستخدم غير موجود");
        }

        Address? addressResult = await _addressData.getAddressData(address.id);

        if (addressResult == null)
        {
            return NotFound("العنوان غير موجود");
        }

        if (addressResult.owner_id != idHolder.Value)
        {
            return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
        }

        AddressResponseDto? location = await _addressData.updateAddress(
            addressId: addressResult.id,
            titile: address.title,
            longitude: address.longitude,
            latitude: address.latitude
        );

        if (location == null)
            return BadRequest("حدثة مشكلة اثناء تعديل البيانات");

        return StatusCode(200, location);
    }

    [HttpDelete("address/{addre_id}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteUserLocation(
        Guid addre_id
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return NotFound("المستخدم غير موجود");
        }

        Address? addressResult = await _addressData.getAddressData(addre_id);

        if (addressResult == null)
        {
            return NotFound("العنوان غير موجود");
        }

        if (addressResult.owner_id != idHolder.Value)
        {
            return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
        }

        if (addressResult.isCurrent)
            return BadRequest("لا يمكن حذف العنوان الحالي");

        bool result = await _addressData.deleteDaddress(
            addressId: addressResult.id);

        if (result == false)
            return BadRequest("حدثة مشكلة اثناء حذف البيانات");

        return NoContent();
    }


    [HttpPost("address/active{addressID:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> setUserLocation(Guid addressID)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }

        AddressResponseDto? address = await _addressData.getUserAddressByAddressId(addressID);

        if (address == null)
        {
            return BadRequest("العنوان غير موجود");
        }

        bool? result = await _addressData.changeCurrentAddress(address.id, user.ID);

        return StatusCode(200, result);
    }
    
    [AllowAnonymous]
    [HttpPost("generateOtp")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> generateOtp([FromBody] ForgetPasswordDto email)
    {
        UserInfoResponseDto? user = await _userData.getUser(email.email);

        if (user == null)
        {
            NotFound("user not exist");
        }

        string otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");

        bool isOtpExist = _forgetPasswordData.isExist(otp);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = _forgetPasswordData.isExist(otp);
            } while (isOtpExist);
        }

        bool result = await _forgetPasswordData.createNewOtp(otp, email.email);
        bool emailSendResult = await _emailService.sendingEmail(email.email, otp);

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
    public async Task<IActionResult> verifingOtp([FromBody] VerificationRequestDto verification)
    {

        bool result = await _forgetPasswordData.isExist(verification.email, verification.otp);
        if (!result)
        {
          return  NotFound("not found otp");
        }

      result = await _forgetPasswordData.updateOtpStatus(verification.otp);

        if (!result)
            return BadRequest("حدثة مشكلة اثناء حفظ البيانات");

        return NoContent();
    }


    [AllowAnonymous]
    [HttpPost("reseatPassword")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> reseatPassword([FromBody] ReseatePasswordRequestDto data)
    {

        bool otpValidationResult = await _forgetPasswordData.isExist(data.email, data.otp,true);
      
        if (!otpValidationResult)
        {
          return  NotFound("not found otp");
        }

        bool isExist =await _userData.isExistByEmail(data.email);
        if (!isExist)
            return NotFound("المستخدم غير موجود");
        UserInfoResponseDto? result = await _userData.updateUserPassword(data.email,
            clsUtil.hashingText(data.password));
        if (result==null)
            return BadRequest("حدثة مشكلة اثناء حفظ البيانات");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.Id,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(200
            , new { token = token, refreshToken = refreshToken });
    }
}