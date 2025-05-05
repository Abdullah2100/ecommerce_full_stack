using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
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
    }

    private readonly AppDbContext _dbContext;
    private readonly ILogger _logger;
    private readonly IConfigurationServices _configuration;

    [AllowAnonymous]
    [HttpPost("signup")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> signUp([FromBody] SignupDto data)
    {
        string? validationResult = clsValidation.validateInput(data.email, data.password, data.phone);

        if (validationResult != null)
        {
            return BadRequest(validationResult);
        }

        var userData = new UserData(_dbContext
            // , _logger
            );
        User? result = await userData.createNew(data);
        if (result == null)
            return BadRequest("some thing went wrong");
        
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
    public IActionResult signUp([FromBody] LoginDto data)
    {

        var userData = new UserData(_dbContext);
        User? result =  userData.isExist(data.username,clsUtil.hashingText(data.password));
        if (result == null)
            return BadRequest("user not found ");
        
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


    
    
    
}