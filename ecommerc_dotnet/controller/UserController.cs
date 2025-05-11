using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.Services.EmailServices;
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
    public UserController(AppDbContext dbContext
        , IConfigurationServices configuration,
        // IEmailServices emailServices,
        IWebHostEnvironment webHostEnvironment
    )
    {
        _dbContext = dbContext;
        _configuration = configuration;
        _userData = new UserData(_dbContext);
        _forgetPasswordData = new ForgetPasswordData(_dbContext);
        // _emailServices = emailServices;
        _host = webHostEnvironment;
        _addressData=new AddressData(_dbContext);
    }

    private readonly AppDbContext _dbContext;
    private readonly IConfigurationServices _configuration;
    // private readonly IEmailServices _emailServices;
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

        if (_userData.isExistByEmail(data.email))
        {
            //return BadRequest("email already exist");
            return BadRequest("email already exist");
        }

        if (_userData.isExistByPhone(data.phone))
        {
            return BadRequest("phone already exist");
        }

        User? result = await _userData.createNew(data);
        if (result == null)
            return BadRequest("هناك مشكلة ما");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(StatusCodes.Status201Created
            , new { token = token, refreshToken = refreshToken });
    }


    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public IActionResult signIn([FromBody] LoginDto data)
    {
        User? result = _userData.getUser(data.username, clsUtil.hashingText(data.password));
        if (result == null)
            return BadRequest("المستخدم غير موجود");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(200
            , new { token = token, refreshToken = refreshToken });
    }


    [HttpDelete("{userID:guid}")]
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

        var currentUser = _userData.getUser(idHolder.Value);

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

        var user = _userData.getUser(idHolder.Value);

        if (user == null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        List<AddressResponseDto>? locations =await _addressData.getUserLocations(user.ID);
        string static_file_url = _configuration.getKey("url_file");

        var userInfo = new UserInfoDto(
            id: user!.ID,
            name: user.name,
            phone: user.phone,
            email: user.email,
            thumbnail:user.thumbnail!=null?static_file_url+ user.thumbnail:"",
            address:locations 
        );


        return StatusCode(200, userInfo);
    }


    [HttpPut("")]
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

        var user = _userData.getUser(idHolder.Value);

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
            
            profile = await clsUtil.saveFile(userData.thumbnail, clsUtil.enImageType.PRODUCT,_host);
        }

        userData.userId = idHolder;
        var result = await _userData.updateUser(userData, profile);

        if (result == null)
            return BadRequest("هناك مشكلة في تحديث البيانات");

        List<AddressResponseDto>? locations =await _addressData.getUserLocations(user.ID);

        string static_file_url = _configuration.getKey("url_file");
        var userInfo = new UserInfoDto(
            id: user!.ID,
            name: user.name,
            phone: user.phone,
            email: user.email,
            thumbnail:user.thumbnail!=null?static_file_url+ user.thumbnail:"",
            address: locations
        );
        return Ok(userInfo);
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
   
  
   [HttpGet("location")]
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

       var user = _userData.getUser(idHolder.Value);

       if (user == null||user.isDeleted)
       {
           return BadRequest("المستخدم غير موجود");
       }

       
       var locations = await _addressData.getUserLocations(user.ID);

       return StatusCode(200, locations);
   }

 
   [HttpPost("location")]
   public async Task<IActionResult> setUserLocation(
   [FromBody]AddressRequestDto address
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

       var user = _userData.getUser(idHolder.Value);

       if (user == null||user.isDeleted)
       {
           return BadRequest("المستخدم غير موجود");
       }

       
       var location = await _addressData.addUserLocation(address,user.ID);
           
       return StatusCode(200, location);
   }

  
   [HttpPost("location/current{addressID:guid}")]
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

       var user = _userData.getUser(idHolder.Value);

       if (user == null||user.isDeleted)
       {
           return BadRequest("المستخدم غير موجود");
       }

       var address = _addressData.getUserLocation(addressID);
       
       if (address==null)
       {
           return BadRequest("العنوان غير موجود");
           
       }
       
       var result = await _addressData.changeCurrentAddress(address.id,user.ID);
           
       return StatusCode(200, result);
   }
 
   
}