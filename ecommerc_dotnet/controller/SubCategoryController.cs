using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
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
        IConfig config
    )
    {
        _categoryData = new CategoryData(dbContext);
        _subCategoryData = new SubCategoryData(dbContext);
        _userData = new UserData(dbContext, config);
    }

    private readonly CategoryData _categoryData;
    private readonly SubCategoryData _subCategoryData;
    private readonly UserData _userData;

    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> creatSubCategory([FromBody] SubCategoryRquestDto subCategory)
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        bool existByName = !await _categoryData.isExist(subCategory.cateogy_id);
        if (existByName == true)
        {
            return NotFound("القسم الذي ادخلته غير موجود");
        }

        UserInfoResponseDto?  user = await _userData.getUser(idHolder.Value);

        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        bool isBlockUser = await _userData.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.store_id;

        if (storeData == null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        int? store_subCateogry_size = await _subCategoryData.countByStoreId(user.store_id);
        if (store_subCateogry_size == null)
            return BadRequest("حدثة مشكلة اثناء التاكد من عدد الفئات الفرعية للمتجر");
        if(store_subCateogry_size>19)
        return BadRequest("لا يمكنك اضافة اكثر من 20 فئة في متجرك");


        SubCategoryResponseDto? result = await _subCategoryData
            .createSubCategory(cateogy_id: subCategory.cateogy_id,
                store_id: (Guid)user.store_id!,
                name: subCategory.name);

        if (result == null)
            return BadRequest("حدثة مشكلة اثناء الفئة");
        return StatusCode(201, result);
    }

    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateSubCategory([FromBody] SubCategoryRquestUpdateDto subCategory)
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid? idHolder = null;
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        UserInfoResponseDto? user = await _userData.getUser(idHolder.Value);

        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }
        
        bool existByName = !await _categoryData.isExist(subCategory.cateogy_id);
        if (existByName == true)
        {
            return NotFound("القسم الذي ادخلته غير مودود");
        }


        bool isBlockUser = await _userData.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.store_id;

        if (storeData == null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        if (await _subCategoryData.isExist((Guid)user.store_id!, (Guid)subCategory.id!) == null)
            return BadRequest("الفئة التي ادخلتها غير موجودة");


        SubCategoryResponseDto? result = await _subCategoryData
            .updateSubCategory(id: subCategory.id,
                name: subCategory.name,
                category_id: subCategory.cateogy_id);

        if (result == null)
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
        if (Guid.TryParse(id.Value.ToString(), out Guid outID))
        {
            idHolder = outID;
        }

        if (idHolder == null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        UserInfoResponseDto? user = await _userData.getUser(idHolder.Value);

        if (user == null)
        {
            return NotFound("المستخدم غير موجود");
        }
        

        bool isBlockUser = await _userData.isExist(idHolder.Value);
        if (!isBlockUser)
        {
            return BadRequest("لا يمكنك انشاء فئة جديد يمكنك مراجعة مدير النظام لحل المشكلة");
        }

        Guid? storeData = user.store_id;

        if (storeData == null)
        {
            return NotFound("ليس لديك اي متجر يرجى فتح متجر لكي تكون قادرا على اضافة فئة");
        }

        if (await _subCategoryData.isExist((Guid)user.store_id!, subCateogyId) == null)
            return BadRequest("الفئة التي ادخلتها غير موجودة");


        bool? result = await _subCategoryData.deleteSubCategory(subCateogyId);

        if (result == null)
            return BadRequest("حدثة مشكلة اثناء حذف الفئة");
        return NoContent();
    }

  
    
    [HttpGet("{store_id}/{page:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getSubCategory(Guid store_id, int page)
    {
        List<SubCategoryResponseDto>? result = await _subCategoryData
            .getSubCategory(store_id, page);
        if (result.Count < 1)
            return NoContent();

        return StatusCode(200, result);
    }
}