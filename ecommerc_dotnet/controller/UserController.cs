using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/User")]
public class UserController : ControllerBase
{
    public UserController(AppDbContext dbContext
        // , ILogger logger
        , IConfigurationServices configuration)
    {
        _dbContext = dbContext;
        // _logger = logger;
        _configuration = configuration;
        _userData = new UserData(_dbContext);
    }

    private readonly AppDbContext _dbContext;
    private readonly ILogger _logger;
    private readonly IConfigurationServices _configuration;
    private readonly UserData _userData;

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

        
        User? result = await _userData.createNew(data);
        if (result == null)
            return BadRequest("هناك مشكلة ما");
        
        string token = "", refreshToken = "";
        
        token = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.person.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.person.email,
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

        User? result =  _userData.getUser(data.username,clsUtil.hashingText(data.password));
        if (result == null)
            return BadRequest("المستخدم غير موجود");
        
        string token = "", refreshToken = "";
        
        token = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.person.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: result.ID,
            email: result.person.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(StatusCodes.Status201Created
            , new { token = token, refreshToken = refreshToken });
    }



    [HttpDelete("{userID:guid}")]
    public async Task<IActionResult> deleteUser(Guid userID)
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
        
        var currentUser = _userData.getUser(idHolder.Value);

        if (userID != idHolder&&(currentUser==null || currentUser.role==1))
        {
            return BadRequest("ليس لديك الصلاحية لحذف الحساب");
        }

        if (currentUser.isDeleted)
        {
            return BadRequest("المستخدم محذوف");
 
        }
        
        bool result = await _userData.deleteUser(idHolder.Value);
        if(result)
            return Ok("تم حذف المستخدم بنجاح");

        return BadRequest(" حدثت مشكلة اثناء الحذف");
 
        
        
    }
    
    [HttpGet("")]
    public async Task<IActionResult> myInfo()
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

        if (user==null)
        {
            return BadRequest("المستخدم غير موجود");
        }

        var userInfo = new UserInfoDto(
            id: user.ID,
            name: user.person.name,
            phone: user.person.phone,
            address: user.person.address,
            email: user.person.email,
            username: user.username
            );
       

        
        return StatusCode(200,userInfo);
 
        
        
    }


    
    
}