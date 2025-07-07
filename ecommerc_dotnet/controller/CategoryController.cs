using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Category")]
public class CategoryController : ControllerBase
{
    public CategoryController(AppDbContext dbContext
        , IConfig configuration,
        IWebHostEnvironment webHostEnvironment
    )
    {
        _host = webHostEnvironment;
        _categoryData = new CategoryData(dbContext);
        _userData = new UserData(dbContext, configuration);
        _configuration = configuration;
    }

    private readonly CategoryData _categoryData;
    private readonly UserData _userData;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getCatgory(int pageNumber = 1)
    {
        if (pageNumber < 1)
            return BadRequest("خطء في البيانات المرسلة");

        List<CategoryResponseDto>? categories = await _categoryData.getCategories(_configuration, pageNumber);
        if (categories == null)
            return NoContent();

        return Ok(categories);
    } 


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> createCateogry([FromForm] CategoryRequestDto category)
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
            return NotFound("المستخدم غير موجود");

        if (user.role == 1)
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        bool isExist = await _categoryData.isExist(category.name);

        if (isExist)
            return Conflict("هناك قسم بهذا الاسم");

        string imagePath = await clsUtil.saveFile(category.image, clsUtil.enImageType.CATEGORY, _host);
        // var imagePath = await MinIoServices.uploadFile(_configuration,category.image,MinIoServices.enBucketName.CATEGORY); ;

        if (imagePath == null)
            return BadRequest("حدثة مشكلة اثناء حفظ الصورة");

        bool? result = await _categoryData.addNewCategory(category.name, imagePath, user.id);

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ القسم");

        return Created();
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateCateogry(
        [FromForm] CategoryRequestUpdatteDto category)
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
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        Category? categoryHolder = await _categoryData.getCategory(category.id);

        if (categoryHolder == null)
            return BadRequest("القسم غير موجود");


        bool isExistName = false;

        if (category?.name != null&&category.name!=categoryHolder.name)
            isExistName = await _categoryData.isExist(category.name);

        if (isExistName && categoryHolder.id != category!.id)
            return Conflict("هناك قسم بهذا الاسم");

        string? imagePath = null;

        if (category?.image != null)
        {
            if (categoryHolder.image != null)
                clsUtil.deleteFile(categoryHolder.image, _host);
            imagePath = await clsUtil.saveFile(category.image, clsUtil.enImageType.CATEGORY, _host);
        }

        bool? result = await _categoryData.updateCategory(
            category!.id,
            categoryHolder.name != category.name ? category.name : null,
            imagePath
        );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ القسم");

        return NoContent();
    }


    [HttpDelete("{categoryId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteCategory(Guid categoryId)
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
            return NotFound("ليس لديك الصلاحية لانشاء قسم جديد");

        if (user.role == 1)
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        Category? categoryHolder = await _categoryData.getCategory(categoryId);

        if (categoryHolder == null)
            return NotFound("القسم غير موجود");

        bool? result = await _categoryData.deleteCategory(
            categoryId);

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حذف القسم");

        return NoContent();
    }
}