using System.Security.Claims;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.dto.Request;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/General")]
public class GeneralController(IGeneralSettingServices generalSettingServices) : ControllerBase
{
    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createGeneralSetting(
        [FromBody] GeneralSettingDto generalSetting
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        var result = await generalSettingServices.createGeneralSetting(
            adminId:adminId.Value,
            generalSetting
            );
        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };   
    }


    [HttpDelete("{genralSettingId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteGeneralSetting(
        Guid genralSettingId 
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

       
        var result = await generalSettingServices.deleteGeneralSetting(
            adminId:adminId.Value,
            id:genralSettingId
        );
        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };   
    }

    [HttpPut("{genralSettingId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> UpdateGeneralSetting(
        Guid genralSettingId ,
        [FromBody] UpdateGeneralSettingDto generalSetting
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? admin = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            admin = outId;
        }

        if (admin is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        
        var result = await generalSettingServices.updateGeneralSetting(
            adminId:admin.Value,
            id:genralSettingId,
            settingDto:generalSetting
        );
        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };   
        
     
    }

    [AllowAnonymous]
    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getGeneralSettings(
         int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await generalSettingServices.getGeneralSettings(
            pageNum:pageNumber,
            pageSize:25
        );
        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };    
    }


  
    
    
}