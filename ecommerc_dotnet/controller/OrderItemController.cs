using System.Security.Claims;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.dto;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;


namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/OrderItems")]
public class OrderItemController(IOrderItemServices orderItemServices) : ControllerBase
{
    [HttpGet("{pageNumber}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getOrdersItemForStore
    (
        int pageNumber = 1
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderItemServices
            .getOrderItmes(
                userId.Value,
                pageNumber,
                25
            );

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpPut("statsu")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateOrderItemStatus
        ([FromBody] UpdateOrderItemStatusDto orderItemStatusDto)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await orderItemServices
            .updateOrderItmesStatus(
                userId.Value,
                orderItemStatusDto);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
}