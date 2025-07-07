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
using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/General")]
public class GeneralController : ControllerBase
{
    public GeneralController(AppDbContext context)
    {
        _generalData = new GeneralData(context);
        _userData = new UserData(context);
    }

    public GeneralData _generalData { get; set; }
    public UserData _userData { get; set; }


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createGeneralSetting(
        [FromBody] GeneralSettingRequestDto generalSetting
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
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

        User? user = await _userData.getUserById(idHolder.Value);

        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.role == 1)
        {
            return NotFound("ليس لديك الصلاحية لاكمال العملية");
        }




        GeneralSettings? result =
            await _generalData.createGeneralSetting(
               generalSetting.name,
               generalSetting.value
                );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حقظ الوحة الاعلانية");

        return StatusCode(201, result);
    }


    [HttpDelete("{genralSettingId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteBanner(
        Guid genralSettingId 
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
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

        User? user = await _userData.getUserById(idHolder.Value);
        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.role == 1)
        {
            return NotFound("ليس لديك الصلاحية لحذف البيانات");
        }

        GeneralSettings? banner = await _generalData
            .getGeneralSettings(genralSettingId);

        if ((banner == null))
        {
            return BadRequest("هذا الاعداد غير موجود");
        }




        bool? result = await _generalData.deleteGeneralSetting(banner.id);

        if (result == false)
            return BadRequest("حدثت مشكلة اثناء حذف الاعداد العام");

        return NoContent();
    }

    [HttpPut("{genralSettingId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updatePanner(
        Guid genralSettingId ,
        [FromBody] GeneralSettingRequestDto generalSetting
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
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

        User? user = await _userData.getUserById(idHolder.Value);
        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.role == 1)
        {
            return NotFound("ليس لديك الصلاحية لحذف البيانات");
        }

        GeneralSettings? generalSettings = await _generalData
            .getGeneralSettings(genralSettingId);

        if ((generalSettings == null))
        {
            return BadRequest("هذا الاعداد غير موجود");
        }




        GeneralSettings? result = await _generalData.updateGeneralSetting(
            genralSettingId,
            generalSettings.name, 
            generalSettings.value);

        if (result ==null)
            return BadRequest("حدثت مشكلة اثناء حذف الاعداد العام");

        return NoContent();
    }

    [AllowAnonymous]
    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBanner(
         int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        List<GeneralSettingResponseDto>? result = await _generalData
            .getGeneralSettingList( pageNumber);

        if (result == null)
            return NoContent();
        return StatusCode(200, result);
    }


  
    
    
}