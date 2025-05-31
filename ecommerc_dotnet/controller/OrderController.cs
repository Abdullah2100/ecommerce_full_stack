using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using FirebaseAdmin.Messaging;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Order")]
public class OrderController : ControllerBase
{
    private readonly AppDbContext _dbContext;
    private readonly OrderData _orderData;
    private readonly UserData _userData;
    private readonly ProductData _productData;
    private readonly IHubContext<EcommercHub> _hubContext;

    public OrderController(
        AppDbContext dbContext,
        IConfig config,
        IWebHostEnvironment host,
        IHubContext<EcommercHub> hubContext)
    {
        _dbContext = dbContext;
        _orderData = new OrderData(dbContext, config);
        _userData = new UserData(dbContext, config);
        _productData = new ProductData(dbContext, config, host);
        _hubContext = hubContext;
    }


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> createOrder
    (
        [FromBody] OrderRequestDto order
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
            return NotFound("المستخدم غير موجود");

        if (user.isDeleted == true)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var isTotalPriceValid = await isValideTotalPrice(order.totalPrice, order.items);

        if (isTotalPriceValid == false)
            return Conflict("اجمالي السعر غير صحيح");

        var result = await _orderData.createOrder(
            userId: idHolder.Value,
            longitude: order.longitude,
            latitude: order.latitude,
            totalPrice: order.totalPrice,
            items: order.items
        );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ الطلب");

        await _hubContext.Clients.All.SendAsync("createdOrder", result);
        var messagin = FirebaseMessaging.DefaultInstance;
        await messagin.SendAsync(new
            Message
            {
                Notification = new Notification
                {
                    Title = "Your order has been created",
                    Body = "Your order has been created",
                },
                Token = user.deviceToken
            });

        return StatusCode(201, result);
    }

    private async Task<bool> isValideTotalPrice(decimal totalPrice, List<OrderRequestItemsDto> items)
    {
        try
        {
            bool isAmbiguous = false;
            decimal realPrice = 0;

            items.ForEach(item =>
            {
                var product = _productData.getProduct(item.product_Id);
                decimal varientPrice = 1;

                item.products_varient_id.ForEach(pvi =>
                {
                    var productVairntPrice = _dbContext.ProductVarients
                        .FirstOrDefault(pv => pv.product_id == product.id && pv.id == pvi);
                    if (productVairntPrice == null)
                    {
                        isAmbiguous = true;
                        return;
                    }

                    varientPrice = varientPrice * productVairntPrice.precentage;
                });
                if (isAmbiguous == true)
                {
                    return;
                }

                if (product.price != item.price)
                {
                    isAmbiguous = true;
                    return;
                }

                realPrice += ((varientPrice * product.price) * item.quanity);
            });
            if (isAmbiguous == true)
            {
                return false;
            }

            return realPrice == totalPrice;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    [HttpGet("all/{pageNumber}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getOrders
    (
        int pageNumber = 1
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
            return NotFound("المستخدم غير موجود");

        if (user.isDeleted == true || user.role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var result = await _orderData.getOrder(pageNumber, 25);
        if (result == null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }

    [HttpGet("me/{pageNumber}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getMyOrders
    (
        int pageNumber = 1
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
            return NotFound("المستخدم غير موجود");

        if (user.isDeleted == true || user.role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var result = await _orderData.getOrder(idHolder.Value, pageNumber, 25);
        if (result == null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }

    [HttpDelete("{order_id}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteOrders
        (Guid order_id)
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
            return NotFound("المستخدم غير موجود");

        if (user.isDeleted == true || user.role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var orderData = await _orderData.getOrder(order_id, idHolder.Value);
        if (orderData == null)
            return NotFound("الطلب غير موجود");
        if (orderData.status != 1)
            return BadRequest("لا يمكن الغاء هذا الطلب لانه تمت معالجته سابقا");
        var result = await _orderData.deleteOrder(idHolder.Value,order_id);
        if (result ==false)
            return BadRequest("حدثة مشكلة اثناء حذف البيانات");
        return NoContent();
    }
}