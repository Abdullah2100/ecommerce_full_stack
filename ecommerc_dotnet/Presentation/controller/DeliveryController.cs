using System.Security.Claims;
using ecommerc_dotnet.application;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.Presentation.controller;

[Authorize]
[ApiController]
[Route("api/Delivery")]
public class DeliveryController(
    IDeliveryServices deliveryServices,
    IOrderServices orderServices,
    IAuthenticationService authenticationService)
    : ControllerBase
{
    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> login([FromBody] LoginDto data)
    {
        var result = await deliveryServices.login(data);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> createDelivery
    ([FromForm] CreateDeliveryDto delivery)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await deliveryServices.createDelivery(
            userId.Value,
            delivery);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getDelivery()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await deliveryServices.getDelivery(userId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPut()]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateDeliveryInfo([FromForm] UpdateDeliveryDto delivery)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? deliveryId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            deliveryId = outId;
        }

        if (deliveryId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await deliveryServices.updateDelivery(delivery, deliveryId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getDeivery(int pageNumber)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? belongToId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            belongToId = outId;
        }

        if (belongToId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await deliveryServices.getDeliveries(belongToId.Value, pageNumber, 25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPatch("{status:bool}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateDeliveryStatus(bool status)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }


        var result = await deliveryServices.updateDeliveryStatus(userId.Value, status);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
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
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? deliveryId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            deliveryId = outId;
        }

        if (deliveryId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderServices
            .getOrdersNotBelongToDeliveries(deliveryId.Value, pageNumber, 25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
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
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? deliveryId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            deliveryId = outId;
        }

        if (deliveryId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderServices.getOrdersbyDeliveryId(
            deliveryId.Value, pageNumber, 25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPatch("{orderId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateOrderDeliveryId(Guid orderId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? deliveryId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            deliveryId = outId;
        }

        if (deliveryId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderServices.submitOrderToDelivery(orderId, deliveryId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpDelete("{orderId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> cencleOrderBelongToDelivery(Guid orderId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? deliveryId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            deliveryId = outId;
        }

        if (deliveryId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderServices.cancelOrderFromDelivery(orderId, deliveryId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
}