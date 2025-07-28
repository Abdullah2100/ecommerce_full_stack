using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.services;
using ecommerc_dotnet.UnitOfWork;
using FirebaseAdmin.Messaging;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Order")]
public class OrderController : ControllerBase
{
    private readonly AppDbContext _dbContext;
    private readonly OrderData _orderData;
    private readonly DeliveryData _deliveryData;
    private readonly UserService _userService;
    private readonly ProductData _productData;
    private readonly IHubContext<EcommerceHub> _hubContext;

    public OrderController(
        AppDbContext dbContext,
        IConfig config,
        IWebHostEnvironment host,
        IHubContext<EcommerceHub> hubContext,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _orderData = new OrderData(dbContext, config, unitOfWork);
        _userService = new UserService(dbContext, config, unitOfWork);
        _productData = new ProductData(dbContext, config, host, unitOfWork);
        _deliveryData = new DeliveryData(dbContext, config, unitOfWork);
        _hubContext = hubContext;
    }


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> createOrder
        ([FromBody] CreateOrderDto order)
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        bool isTotalPriceValid = isValideTotalPrice(order.TotalPrice, order.Items);

        if (isTotalPriceValid == false)
            return Conflict("اجمالي السعر غير صحيح");


        var result = await _orderData.createOrder(
            userId: idHolder.Value,
            longitude: order.Longitude,
            latitude: order.Latitude,
            totalPrice: order.TotalPrice,
            items: order.Items
        );

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء حفظ الطلب");

        await _hubContext.Clients.All.SendAsync("createdOrder", result);

        sendingFireBaseMessage(user.deviceToken, "تم انشاء الطلب بنجاح");
        return StatusCode(201, result);
    }

    private async void sendingFireBaseMessage(string token, string message)
    {
        try
        {
            var messagin = FirebaseMessaging.DefaultInstance;

            await messagin.SendAsync(new
                Message

                {
                    Notification = new Notification
                    {
                        Title = "Your order has been created",
                        Body = "Your order has been created",
                    },
                    Token = token
                });
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this errror from sending firebase message {ex.Message}");
        }
    }

    private bool isValideTotalPrice(decimal totalPrice, List<CreateOrderItemDto> items)
    {
        try
        {
            bool isAmbiguous = false;
            decimal realPrice = 0;

            items.ForEach(item =>
            {
                var product = _productData.getProduct(item.ProductId);
                decimal varientPrice = 1;

                item.ProductsVarientId?.ForEach(pvi =>
                {
                    var productVairntPrice = _dbContext.ProductVarients
                        .FirstOrDefault(pv => pv.ProductId == product!.Id && pv.Id == pvi);
                    if (productVairntPrice is null)
                    {
                        isAmbiguous = true;
                        return;
                    }

                    varientPrice = varientPrice * productVairntPrice.Precentage;
                });
                if (isAmbiguous == true)
                {
                    return;
                }

                if (product.Price != item.Price)
                {
                    isAmbiguous = true;
                    return;
                }

                realPrice += ((varientPrice * product.Price) * item.Quantity);
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true || user.Role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var result = await _orderData.getOrder(pageNumber, 25);
        if (result is null || result.Count < 1)
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true || user.Role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        List<OrderDto>? result = await _orderData.getOrder(idHolder.Value, pageNumber, 25);
        if (result is null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }


    [HttpDelete("{orderId}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteOrders
        (Guid orderId)
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true || user.Role == 1)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        var orderData = await _orderData.getOrder(orderId, idHolder.Value);
        if (orderData is null)
            return NotFound("الطلب غير موجود");
        if (orderData.Status != 1)
            return BadRequest("لا يمكن الغاء هذا الطلب لانه تمت معالجته سابقا");
        var result = await _orderData.deleteOrder(idHolder.Value, orderId);
        if (result == false)
            return BadRequest("حدثة مشكلة اثناء حذف البيانات");
        return NoContent();
    }


    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getOrderPages()
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Role == 1)
            return BadRequest("فقط مدير النظام لديه الصلاحية");

        var pages = await _orderData.getOrdersSize();

        return Ok(pages);
    }


    [HttpPut()]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateOrderStatus
    (
        [FromBody] UpdateOrderStatusDto orderStatus
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Role == 1)
            return BadRequest("فقط مدير النظام لديه الصلاحية");

        if (orderStatus.Status > 6 || orderStatus.Status < 0)
            return BadRequest("رقم الحالة الذي تم ادخالة غير مدرج");

        var result = await _orderData.updateOrderStatus(
            orderStatus.Id,
            orderStatus.Status);
        if (result == false)
            return BadRequest("حدثة مشكلة اثناء تعديل الحالة");

        if (orderStatus.Status == 2)
        {
            var order = await _orderData.getOrder(orderStatus.Id);
            if (order is null)
                return NotFound();
            await _hubContext.Clients.All.SendAsync("orderExcptedByAdmin", order);
            List<Guid> storesId = order.OrderItems
                .DistinctBy(or => or.Product.StoreId)
                .Select(or => or.Product.StoreId).ToList();
            
        }

        return NoContent();
    }


    [HttpGet("orderStatusDeffination")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getOrderStatus()
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");


        return Ok(OrderData.orderStatusDefination);
    }


    [HttpGet("orderItem/{storeId}/{pageNumber}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getOrdersItemForStore
    (
        Guid storeId,
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        List<OrderItemDto>? result = await _orderData.getOrderItems(storeId, pageNumber, 25);
        if (result is null || result.Count < 1)
            return NoContent();
        return StatusCode(200, result);
    }

    [HttpPut("orderItem/statsu")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateOrderItemStatus
        ([FromBody] UpdateOrderItemStatusDto orderItemStatusDto)
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

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.IsBlocked == true)
            return BadRequest("تم حظر المستخدم من اجراء اي عمليات يرجى مراجعة مدير الانظام");

        if (user.Store is null)
            return BadRequest("المستخدم لا يمتلك اي متجر");

        var orderItem = await _orderData.getOrderItem(orderItemStatusDto.Id, user.Store.Id);
        if (orderItem is null)
            return NotFound("الطلب غير موجود");
        var result = await _orderData.updateOrderItemStatus(
            orderItemStatusDto.Id,
            orderItemStatusDto.Status);

        if (result == false)
            return BadRequest("حدثة مشكلة اثناء تغير حالة الطلب");

        OrderItemsStatusEvent status = new OrderItemsStatusEvent 
        {
            OrderId = orderItem.OrderId,
            OrderItemId = orderItem.Id,
            Status = enOrderItemStatus.Excepted.ToString()
        };
        await _hubContext.Clients.All.SendAsync("orderItemsStatusChange", status);

        return NoContent();
    }

 
    
}