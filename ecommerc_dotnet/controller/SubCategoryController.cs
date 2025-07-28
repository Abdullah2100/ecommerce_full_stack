using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/SubCategory")]
public class SubCategoryController : ControllerBase
{
    public SubCategoryController
    (
        AppDbContext dbContext,
        IConfig config,
        IUnitOfWork unitOfWork
    )
    {
        _categoryData = new CategoryData(dbContext,unitOfWork,config);
        _subCategoryData = new SubCategoryData(dbContext, unitOfWork);
        _userService = new UserService(dbContext, config,unitOfWork);
    }

    private readonly CategoryData _categoryData;
    private readonly SubCategoryData _subCategoryData;
    private readonly UserService _userService;

    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> creatSubCategory([FromBody] CreateSubCategoryDto subCategory)
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        bool existByName = !await _categoryData.isExist(subCategory.CategoryId);
        if (existByName == true)
        {
            return NotFound("القسم الذي ادخلته غير موجود");
        }

        User?  user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        bool isBlockUser = await _userService.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.Store?.Id;

        if (storeData is null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        int? storeSubCateogrySize = await _subCategoryData.countByStoreId(user.Store?.Id);
        if (storeSubCateogrySize is null)
            return BadRequest("حدثة مشكلة اثناء التاكد من عدد الفئات الفرعية للمتجر");
        if(storeSubCateogrySize>19)
        return BadRequest("لا يمكنك اضافة اكثر من 20 فئة في متجرك");


        SubCategoryDto? result = await _subCategoryData
            .createSubCategory(cateogyId: subCategory.CategoryId,
                storeId: (Guid)user.Store?.Id!,
                name: subCategory.Name);

        if (result is null)
            return BadRequest("حدثة مشكلة اثناء الفئة");
        return StatusCode(201, result);
    }

    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateSubCategory([FromBody] UpdateSubCategoryDto subCategory)
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            idHolder = outId;
        }

        if (idHolder is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        if (subCategory.isEmpty()) return Ok("لا يوجد اي شئ يحتاج للتعديل");
        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return NotFound("المستخدم غير موجود");
        }
        
        bool existByName = !await _categoryData.isExist(subCategory.CateogyId??Guid.Empty);
        if (existByName == true)
        {
            return NotFound("القسم الذي ادخلته غير مودود");
        }


        bool isBlockUser = await _userService.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.Store?.Id;

        if (storeData is null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        if (await _subCategoryData.isExist((Guid)user.Store?.Id!, (Guid)subCategory.id!) == false)
            return BadRequest("الفئة التي ادخلتها غير موجودة");


        SubCategoryDto? result = await _subCategoryData
            .updateSubCategory(id: subCategory.id,
                name: subCategory.Name,
                categoryId: subCategory.CateogyId);

        if (result is null)
            return BadRequest("حدثة مشكلة اثناء الفئة");
        return StatusCode(200, result);
    }

     [HttpDelete("{subCateogyId}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteSubCategory
        (
            Guid subCateogyId 
            )
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid? idHolder = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
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
        

        bool isBlockUser = await _userService.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.Store?.Id;

        if (storeData is null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        if (await _subCategoryData.isExist((Guid)user.Store?.Id!, subCateogyId) == false)
            return BadRequest("الفئة التي ادخلتها غير موجودة");


        bool? result = await _subCategoryData.deleteSubCategory(subCateogyId);

        if (result is null)
            return BadRequest("حدثة مشكلة اثناء حذف الفئة");
        return NoContent();
    }

  
    
    [HttpGet("{storeId}/{page:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getSubCategory(Guid storeId, int page)
    {
        List<SubCategoryDto>? result = await _subCategoryData
            .getSubCategory(storeId, page);
        if (result.Count < 1)
            return NoContent();

        return StatusCode(200, result);
    }
}