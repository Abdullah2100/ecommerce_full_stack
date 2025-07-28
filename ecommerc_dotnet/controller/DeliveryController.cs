using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.services;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Delivery")]
public class DeliveryController : ControllerBase
{
    public DeliveryController(AppDbContext context,
        IWebHostEnvironment host,
        IConfig config,
        IHubContext<EcommerceHub> hubContext,
        IUnitOfWork unitOfWork
    )
    {
        _userService = new UserService(context, config, unitOfWork);
        _orderData = new OrderData(context, config, unitOfWork);

        _deliveryData = new DeliveryData(context, config, unitOfWork);
        _host = host;
        _config = config;
        _hubContext = hubContext;
    }

    private readonly IHubContext<EcommerceHub> _hubContext;

    private readonly UserService _userService;
    private readonly OrderData _orderData;
    private readonly DeliveryData _deliveryData;
    private readonly IWebHostEnvironment _host;
    private readonly IConfig _config;


    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> signIn([FromBody] LoginDto data)
    {
        UserInfoDto? result = await _userService.getUser(data.Username, clsUtil.hashingText(data.Password));

        if (result is null)
            return BadRequest("المستخدم غير موجود");

        DeliveryDto? delivery = await _deliveryData.getInfoByUserId(result.Id);
        if (delivery is null)
            return BadRequest("الموصل غير موجود");


        var updateDeviceTokenResult = await _deliveryData.updateDeviceToken(result.Id, data.DeviceToken);

        if (updateDeviceTokenResult == false)
            return BadRequest("لا بد من تسجيل الدخول باستخدام هاتف");

        string token = "", refreshToken = "";

        token = AuthinticationServices.generateToken(
            userId: delivery.Id,
            email: result.Email,
            _config);

        refreshToken = AuthinticationServices.generateToken(
            userId: delivery.Id,
            email: result.Email,
            _config,
            EnTokenMode.RefreshToken);

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
        [FromForm] CreateDeliveryDto delivery
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

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Role == 1)
            return NotFound("ليس لديك الصلاحية لإكمال العملية");

        bool isExist = await _deliveryData.isExistById(delivery.UserId);

        if (isExist)
            return BadRequest("المستخدم بالفعل مرتبط بموصل");

        string? deliveryThumnail = null;
        if (delivery.Thumbnail is not null)
        {
            deliveryThumnail = await clsUtil.saveFile(delivery.Thumbnail, EnImageType.DELIVERY, _host);
        }

        bool? result = await _deliveryData.createDelivery(
            userId: delivery.UserId,
            deviceToken: delivery.DeviceToken,
            thumbnail: deliveryThumnail,
            longitude: delivery.Longitude,
            latitude: delivery.Latitude
        );


        if (result is null)
        {
            if (delivery.Thumbnail is not null)
            {
                clsUtil.deleteFile(deliveryThumnail ?? "", _host);
            }

            return BadRequest("حدثة مشكلة اثناء اضافة الموصل");
        }

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

        DeliveryDto? delivery = await _deliveryData.getInfoById(idHolder.Value);

        if (delivery is null)
        {
            return BadRequest("الموصل غير موجود");
        }


        return StatusCode(200, delivery);
    }


    [HttpPatch("{status:bool}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateDeliveryStatus(bool status)
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


        DeliveryDto? delivery = await _deliveryData.getInfoById(idHolder.Value);

        if (delivery is null)
        {
            return BadRequest("الموصل غير موجود");
        }

        bool isUpdated = await _deliveryData.updateStatus(
            id: delivery.Id,
            isAviable: status
        );

        if (isUpdated == false)
        {
            return BadRequest("حدثة مشكلة اثناء تحديث حالة الموصل");
        }


        return NoContent();
    }


    [HttpGet("{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getOrderNotTakedByDelivery
    (
        int pageNumber = 1
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

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

        bool isDeliveryMan = await _deliveryData.isExistById(idHolder.Value);

        if (isDeliveryMan == false)
            return NotFound("الموصل غير موجو");


        var result = await _orderData.getOrdersNotBelongToDeliveries(pageNumber, 25);
        if (result is null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }


    [HttpGet("me/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getOrderBelongToMe
    (
        int pageNumber = 1
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

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

        bool isDeliveryMan = await _deliveryData.isExistById(idHolder.Value);

        if (isDeliveryMan == false)
            return NotFound("الموصل غير موجو");


        var result = await _orderData.getOrdersBelongToDeliveries(idHolder.Value, pageNumber, 25);
        if (result is null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }


    [HttpPatch("{orderId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateOrderDeliveryId(Guid orderId)
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

        DeliveryDto? delivery = await _deliveryData.getInfoById(idHolder.Value);

        if (delivery is null)
        {
            return NotFound("الموصل غير موجود");
        }

        Order? order = await _orderData.getOrderById(orderId);

        if (order is null)
        {
            return NotFound("الطلب غير موجود");
        }

        if (order.DeleveryId is not null)
        {
            return BadRequest("تم اخذ الطلب من قبل موصل اخر");
        }

        bool? isUpdated = await _orderData.submitOrderToDeliveryId(
            orderId: orderId,
            deliveryId: idHolder.Value
        );

        if (isUpdated is null)
        {
            return BadRequest("حدثة مشكلة اثناء اضافة التطلب الى الموصل");
        }

        OrderTakedByEvent eventHolder = new OrderTakedByEvent
        {
            Id = order.Id,
            DeliveryId = idHolder.Value
        };
        await _hubContext.Clients.All.SendAsync("orderGettingByDelivery", eventHolder);


        return NoContent();
    }


    [HttpDelete("{orderId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> cencleOrderBelongToDelivery(Guid orderId)
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

        DeliveryDto? delivery = await _deliveryData.getInfoById(idHolder.Value);

        if (delivery is null)
        {
            return NotFound("الموصل غير موجود");
        }

        bool? isExistOrder = await _orderData.isExist(orderId);
        if (isExistOrder is null)
        {
            return NotFound("الطلب غير موجود");
        }

        bool? isOrderCanCelcle = await _orderData.isOrderCanCencled(orderId);

        if (isOrderCanCelcle is null || isOrderCanCelcle == true)
        {
            return BadRequest("لا يمكن  الغاء هذا الطلب بسبب ان  عنصر تم استلامه بالفعل من الموصل");
        }

        bool? isUpdated = await _orderData.removeDeliveryFromCurrentOrder(
            orderId: orderId);

        if (isUpdated is null)
        {
            // return BadRequest("حدثة مشكلة اثناء تحديث حالة الموصل");
            return BadRequest("حدثة مشكلة اثناء ازالة  التطلب من الموصل");
        }

        OrderDto? order = await _orderData.getOrder(orderId);

        if (order is null)
        {
            return BadRequest("الطلب غير موجود");
        }

        await _hubContext.Clients.All.SendAsync("createdOrder", order);

        return NoContent();
    }
}