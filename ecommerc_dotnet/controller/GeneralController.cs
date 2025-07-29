using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.entity;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
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
[Route("api/General")]
public class GeneralController : ControllerBase
{
    public GeneralController(
        AppDbContext context,
        IUnitOfWork unitOfWork,
        IConfig config
        )
    {
        _generalData = new GeneralData(context,unitOfWork);
        _userService = new UserService(context, config,unitOfWork);
    }

    private readonly GeneralData _generalData;
    private readonly UserService _userService;


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
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.Role == 1)
        {
            return NotFound("ليس لديك الصلاحية لاكمال العملية");
        }




        GeneralSetting? result =
            await _generalData.createGeneralSetting(
               generalSetting.Name,
               generalSetting.Value
                );

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء حقظ الوحة الاعلانية");

        return StatusCode(201, result);
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
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.Role == 1)
        {
            return NotFound("ليس لديك الصلاحية لحذف البيانات");
        }

        GeneralSetting? banner = await _generalData
            .getGeneralSettings(genralSettingId);

        if ((banner is null))
        {
            return BadRequest("هذا الاعداد غير موجود");
        }




        bool? result = await _generalData.deleteGeneralSetting(banner.Id);

        if (result == false)
            return BadRequest("حدثت مشكلة اثناء حذف الاعداد العام");

        return NoContent();
    }

    [HttpPut("{genralSettingId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> UpdateGeneralSetting(
        Guid genralSettingId ,
        [FromBody] GeneralSettingDto generalSetting
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.Role == 1)
        {
            return NotFound("ليس لديك الصلاحية لحذف البيانات");
        }

        GeneralSetting? generalSettings = await _generalData
            .getGeneralSettings(genralSettingId);

        if ((generalSettings is null))
        {
            return BadRequest("هذا الاعداد غير موجود");
        }




        GeneralSetting? result = await _generalData.updateGeneralSetting(
            genralSettingId,
            generalSettings.Name, 
            generalSettings.Value);

        if (result ==null)
            return BadRequest("حدثت مشكلة اثناء حذف الاعداد العام");

        return NoContent();
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

        List<GeneralSettingDto>? result = await _generalData
            .getGeneralSettingList( pageNumber);

        if (result is null)
            return NoContent();
        return StatusCode(200, result);
    }


  
    
    
}