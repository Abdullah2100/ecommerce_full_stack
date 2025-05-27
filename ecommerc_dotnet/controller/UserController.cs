using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/User")]
public class UserController : ControllerBase
{
    public UserController(
        AppDbContext dbContext,
        IConfig configuration,
        IWebHostEnvironment webHostEnvironment
    )
    {
        _configuration = configuration;
        _userData = new UserData(dbContext, configuration);
        _host = webHostEnvironment;
        _addressData = new AddressData(dbContext);
    }

    private readonly IConfig _configuration;

    private readonly UserData _userData;

    // private readonly ForgetPasswordData _forgetPasswordData;
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
            role: data.role
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


    [HttpDelete("{userID:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]

    public async Task<IActionResult> deleteUser(Guid userID)
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var currentUser = await _userData.getUserById(idHolder.Value);

        if (currentUser == null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        if (userID != idHolder && (currentUser == null || currentUser.role == 1))
        {
            return BadRequest("ليس لديك الصلاحية لحذف الحساب");
        }

        if (currentUser.isDeleted)
        {
            return BadRequest("المستخدم محذوف");
        }

        bool result = await _userData.deleteUser(idHolder.Value);
        if (result)
            return Ok("تم حذف المستخدم بنجاح");

        return BadRequest(" حدثت مشكلة اثناء الحذف");
    }

    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUser()
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user =await _userData.getUser(idHolder.Value);

        if (user == null)
        {
            return BadRequest("المستخدم غير موجود");
        }


        return StatusCode(200, user);
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateUser(
        [FromForm] UpdateUserInfo userData
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

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

        userData.userId = idHolder;
        var result = await _userData.updateUser(
            userId: idHolder.Value,
            phone: userData.phone,
            password:userData.password==null?null: clsUtil.hashingText(userData.password),
            name: userData.name,
            profile);

        if (result == null)
            return BadRequest("هناك مشكلة في تحديث البيانات");


        return Ok(result);
    }


    //sendOtp
    /* [AllowAnonymous]
     [HttpPost("forgetPasswordOtp")]
     public async Task<IActionResult> forgetPassword([FromBody] ForgetPasswordDto email)
     {
         User? user = _userData.getUser(email.email);

         if (user == null)
         {
             BadRequest("user not exist");
         }

         string otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");

         var isOtpExist = _forgetPasswordData.isExist(otp);
         bool isExist = isOtpExist;

         if (isExist == true)
         {
             do
             {,
   "server": "smtp.elasticemail.com",
   "username": "ali735501225@gmail.com",
   "password": "8CECFFB7A55CF0C3FC26E857C472B6763BC9",
   "port": "2525"
                 otp = clsUtil.generateGuid().ToString().Substring(0, 6).Replace("-", "");
                 isOtpExist = _forgetPasswordData.isExist(otp);
             } while (isOtpExist);
         }

         var result = await _emailServices.sendingEmail(otp, email.email);

         if (result == false)
             return BadRequest("some thing wrong");
         return StatusCode(200, user);
     }
     */


    [HttpGet("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUserLocations()
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }


        var locations = await _addressData.getUserAddressByUserId(user.ID);

        return StatusCode(200, locations);
    }


    [HttpPost("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> setUserLocation(
        [FromBody] AddressRequestDto address
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }

        var userLocationCount = await _addressData.addressCountForUser(idHolder.Value);
        if (userLocationCount > 10)
            return BadRequest("اقصى حد للاماكن التي يمكن للمستخدم ادخالها هي 10");

        var location = await _addressData.addUserLocation(
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
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> updateUserLocation(
        [FromBody] AddressRequestUpdateDto address
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }
        var addressResult = await _addressData.getAddressData(address.id);

        if (addressResult == null)
        {
            return BadRequest("العنوان غير موجود");
        }
       if(addressResult.owner_id!=idHolder.Value)
       {
           return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
       }

        var location = await _addressData.updateAddress(
           addressId:addressResult.id,    
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
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> deleteUserLocation(
        Guid addre_id
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }
        var addressResult = await _addressData.getAddressData(addre_id);

        if (addressResult == null)
        {
            return BadRequest("العنوان غير موجود");
        }
        if(addressResult.owner_id!=idHolder.Value)
        {
            return BadRequest("فقط صاحب العنوان بامكانه تعديل البيانات");
        }
        
        if (addressResult.isCurrent)
            return BadRequest("لا يمكن حذف العنوان الحالي");
 
        var result = await _addressData.deleteDaddress(
            addressId:addressResult.id);
        
        if (result == null)
            return BadRequest("حدثة مشكلة اثناء حذف البيانات");

        return StatusCode(200, result);
    }



    [HttpPost("address/active{addressID:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> setUserLocation(Guid addressID)
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUserById(idHolder.Value);

        if (user == null || user.isDeleted)
        {
            return BadRequest("المستخدم غير موجود");
        }

        var address = await _addressData.getUserAddressByAddressId(addressID);

        if (address == null)
        {
            return BadRequest("العنوان غير موجود");
        }

        var result = await _addressData.changeCurrentAddress(address.id, user.ID);

        return StatusCode(200, result);
    }

}