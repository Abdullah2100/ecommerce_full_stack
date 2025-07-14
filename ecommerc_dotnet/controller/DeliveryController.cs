using System.Security.Claims;
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
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;


[Authorize]
[ApiController]
[Route("api/Delivery")]
public class DeliveryController:ControllerBase
{
    public DeliveryController(AppDbContext context,
        IWebHostEnvironment environment,
        IConfig configuration)
    {
        _userData = new UserData(context);
        _deliveryData = new DeliveryData(context,configuration);
        _context = context;
        _environment = environment;
        _configuration = configuration;
    }

    private readonly UserData _userData;
    private readonly DeliveryData _deliveryData;
    private readonly AppDbContext _context;
    private readonly IWebHostEnvironment  _environment;
    private readonly IConfig _configuration;

    
   [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> signIn([FromBody] LoginDto data)
    {
        UserInfoResponseDto? result = await _userData.getUser(data.username, clsUtil.hashingText(data.password));
        
        if (result is null)
            return BadRequest("المستخدم غير موجود");
        
        DeliveryInfoResponseDto? delivery =await _deliveryData.getInfoByUserId(result.Id);
        if (delivery is null)
            return BadRequest("الموصل غير موجود");


        var updateDeviceTokenResult =await _deliveryData.updateDeliveryDeviceToken(result.Id,data.deviceToken);
        
        if (updateDeviceTokenResult is null)
            return BadRequest("لا بد من تسجيل الدخول باستخدام هاتف");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userID: delivery.id,
            email: result.email,
            _configuration);

        refreshToken = AuthinticationServices.generateToken(
            userID: delivery.id,
            email: result.email,
            _configuration,
            AuthinticationServices.enTokenMode.RefreshToken);

        return StatusCode(200
            , new { token = token, refreshToken = refreshToken });
    }




    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> createDelivery
        (
           [FromForm] DeliveryRequestDto delivery
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

        User? user = await _userData.getUserById(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");
        
        if(user.role==1)
            return NotFound("ليس لديك الصلاحية لإكمال العملية");

        bool isExist = await _deliveryData.isExistById(delivery.userId);

        if (isExist)
            return BadRequest("المستخدم بالفعل مرتبط بموصل");

        string? deliveryThumnail=null;
        if (delivery.thumbnail is not null)
        {
           deliveryThumnail=await  clsUtil.saveFile(delivery.thumbnail,clsUtil.enImageType.DELIVERY,_environment);
        }
        
        bool? result = await _deliveryData.createDelivery(
            userId: delivery.userId,
            deviceToken: delivery.deviceToken,
            thumbnail: deliveryThumnail,
            longitude: delivery.longitude,
            latitude: delivery.latitude
        );
        
        

        if (result is null)
            return BadRequest("حدثة مشكلة اثناء اضافة الموصل");

        return Created();
    }
    
    
    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getDeivery()
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

        DeliveryInfoResponseDto? delivery = await _deliveryData.getInfoById(idHolder.Value);

        if (delivery is null)
        {
            return BadRequest("الموصل غير موجود");
        }


        return StatusCode(200, delivery);
    }

    

}