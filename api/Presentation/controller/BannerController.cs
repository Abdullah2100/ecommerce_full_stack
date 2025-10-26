using System.Security.Claims;
using api.application.Interface;
using api.Presentation.dto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace api.Presentation.controller;

[Authorize]
[ApiController]
[Route("api/Banner")]
public class BannerController(
    IBannerSerivces bannerSerivces,
    IAuthenticationService authenticationService) : ControllerBase
{
    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> CreateBanner(
        [FromForm] CreateBannerDto banner
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
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

        var result = await bannerSerivces.CreateBanner(userId.Value, banner);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpDelete("{bannerId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> DeleteBanner(
        Guid bannerId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
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

        var result = await bannerSerivces
            .DeleteBanner(bannerId, userId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpGet("{storeId:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> GetBanner(
        Guid storeId, int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await bannerSerivces
            .GetBanners(storeId, pageNumber, 25);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    //this method for dashboard only
    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> GetBannerRandom(int pageNumber)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await bannerSerivces
            .GetBanners(adminId.Value, pageNumber, 25);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> GetBannerRandom()
    {
        var result = await bannerSerivces
            .GetBanners(15);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
}