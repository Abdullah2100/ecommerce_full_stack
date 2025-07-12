using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
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
[Route("api/Varient")]
public class VarientController : ControllerBase
{
    public VarientController(AppDbContext appDbContext)
    {
        _userData = new UserData(appDbContext);
        _varientData = new VarientData(appDbContext);
    }

    private readonly UserData _userData;
    private readonly VarientData _varientData;


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createVarient([FromBody] VarientRequestDto varient)
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

        User? userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.role !=  0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }


        var isExist = await _varientData.isExist(varient.name);

        if (isExist)
            return Conflict("هذا الخيار تم ادخاله سابقا");


        VarientResposeDto? result = await _varientData.createVarient(varient.name);

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء حفظ الخيار");

        return StatusCode(201, result);
    }

    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> updateVarient([FromBody] VarientRequestDto varient)
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

        var userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.role !=  0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }

        if (varient.id is null)
            return NotFound("هذا الخيار غير موجود");

        var isExistByID = await _varientData.isExist((Guid)varient.id!);

        if (!isExistByID)
            return NotFound("هذا الخيار غير موجود");

        var isExistByName = await _varientData.isExist(varient.name);

        if (isExistByName)
            return Conflict("هذا الخيار تم ادخاله سابقا");


        var result = await _varientData.updateVarient(varient.name, (Guid)varient.id!);

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء  تعديل  الخيار");

        return StatusCode(200, result);
    }

    [HttpDelete("{varientId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteVarient(Guid varientId)
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

        var userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.role !=  0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }


        var isExistByID = await _varientData.isExist(varientId);

        if (!isExistByID)
            return NotFound("هذا الخيار غير موجود");


        var result = await _varientData.deleteVarient(varientId);

        if (result == false)
            return BadRequest("يحتوي هذا الخيار على علاقات مرتبطة به يجب اولا حذف العناصر المرتبطة به اولا قبل حذفه");

        return NoContent();
    }


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getVarients(int pageNumber = 1)
    {
        if(pageNumber<1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");
        
        var result = await _varientData.getVarients(pageNumber);
        if (result.Count < 1)
            return NoContent();
        return Ok(result);
    }
   
    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getVarients()
    {
        var result = await _varientData.getVarients();
        return Ok(result);
    }

    
}