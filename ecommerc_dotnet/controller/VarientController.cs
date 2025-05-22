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
    public async Task<IActionResult> createVarient([FromBody] VarientRequestDto varient)
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

        var userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder == null)
        {
            return Unauthorized("المستخدم غير موجود");
        }

        if (userHolder.role != 0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }


        var isExist = await _varientData.isExist(varient.name);

        if (isExist)
            return BadRequest("هذا الخيار تم ادخاله سابقا");


        var result = await _varientData.createVarient(varient.name);

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ الخيار");

        return StatusCode(201, result);
    }

    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateVarient([FromBody] VarientRequestDto varient)
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

        var userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder == null)
        {
            return Unauthorized("المستخدم غير موجود");
        }

        if (userHolder.role != 0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }

        if (varient.id == null)
            return BadRequest("هذا الخيار غير موجود");

        var isExistByID = await _varientData.isExist((Guid)varient.id!);

        if (!isExistByID)
            return BadRequest("هذا الخيار غير موجود");

        var isExistByName = await _varientData.isExist(varient.name);

        if (isExistByName)
            return BadRequest("هذا الخيار تم ادخاله سابقا");


        var result = await _varientData.updateVarient(varient.name, (Guid)varient.id!);

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء  تعديل  الخيار");

        return StatusCode(200, result);
    }

    [HttpDelete("{varient_id:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> deleteVarient(Guid varient_id)
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

        var userHolder = await _userData.getUserById(idHolder.Value);
        if (userHolder == null)
        {
            return Unauthorized("المستخدم غير موجود");
        }

        if (userHolder.role != 0)
        {
            return BadRequest("ليس لديك الصلاحية لانشاء خيار جديد");
        }


        var isExistByID = await _varientData.isExist(varient_id);

        if (!isExistByID)
            return BadRequest("هذا الخيار غير موجود");


        var result = await _varientData.deleteVarient(varient_id);

        if (result == false)
            return BadRequest("يحتوي هذا الخيار على علاقات مرتبطة به يجب اولا حذف العناصر المرتبطة به اولا قبل حذفه");

        return StatusCode(200, "تم الحذف بنجاح");
    }


    [HttpGet("all/{pageNumber:int}")]

    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getVarients(int pageNumber = 1)
    {
        var result = await _varientData.getVarients(pageNumber);
        return Ok(result);
    }
    
}