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
[Route("api/Category")]
public class CategoryController : ControllerBase
{
    public CategoryController(AppDbContext dbContext
        , IConfig config,
        IWebHostEnvironment webHostEnvironment,
        IUnitOfWork unitOfWork
    )
    {
        _host = webHostEnvironment;
        _categoryData = new CategoryData(dbContext,unitOfWork,config);
        _userService = new UserService(dbContext, config,unitOfWork);
        _config = config;
    }

    private readonly CategoryData _categoryData;
    private readonly UserService _userService;
    private readonly IConfig _config;
    private readonly IWebHostEnvironment _host;


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getCatgory(int pageNumber = 1)
    {
        if (pageNumber < 1)
            return BadRequest("خطء في البيانات المرسلة");

        List<CategoryDto>? categories = await _categoryData
            .getCategories( pageNumber);
        if (categories is null)
            return NoContent();

        return Ok(categories);
    } 


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> createCateogry([FromForm] CreateCategoryto category)
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

        User? user = await _userService.getUser(idHolder.Value);
        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Role == 1)
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        bool isExist = await _categoryData.isExist(category.Name);

        if (isExist)
            return Conflict("هناك قسم بهذا الاسم");

        string? imagePath = await clsUtil.saveFile(category.Image, EnImageType.CATEGORY, _host);

        if (imagePath is null)
            return BadRequest("حدثة مشكلة اثناء حفظ الصورة");

        bool result = await _categoryData.addNewCategory(category.Name, imagePath, user.Id);

        if (result == false)
        {
            clsUtil.deleteFile(imagePath ?? "", _host);
            return BadRequest("حدثت مشكلة اثناء حفظ القسم");
        }

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
        [FromForm] UpdateCategoryDto category)
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

        if (category.isEmpty()) return Ok("ليس هناك اي تحديث");
        

        User? user = await _userService.getUser(idHolder.Value);

        if (user is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (user.Role == 1)
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        Category? categoryHolder = await _categoryData.getCategory(category.Id);

        if (categoryHolder is null)
            return BadRequest("القسم غير موجود");


        bool isExistName = false;

        if (category?.Name !=  null&&category.Name!= categoryHolder.Name)
            isExistName = await _categoryData.isExist(category.Name);

        if (isExistName && categoryHolder.Id !=  category!.Id)
            return Conflict("هناك قسم بهذا الاسم");

        string? imagePath = null;

        if (category?.Image is not  null)
        {
            if (categoryHolder?.Image is not  null)
                clsUtil.deleteFile(categoryHolder.Image, _host);
            imagePath = await clsUtil.saveFile(category.Image, EnImageType.CATEGORY, _host);
        }

        CategoryDto? result = await _categoryData.updateCategory(
            category!.Id,
            category.Name ,
            imagePath
        );

        if (result is null)
        {
            if (categoryHolder?.Image is not null)
            {
                clsUtil.deleteFile(imagePath??"", _host);
            }
            return BadRequest("حدثت مشكلة اثناء حفظ القسم");
        }

        return Ok(result);
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
            return NotFound("ليس لديك الصلاحية لانشاء قسم جديد");

        if (user.Role == 1)
            return BadRequest("ليس لديك الصلاحية لانشاء قسم جديد");

        Category? categoryHolder = await _categoryData.getCategory(categoryId);

        if (categoryHolder is null)
            return NotFound("القسم غير موجود");

        bool? result = await _categoryData.deleteCategory(
            categoryId);

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء حذف القسم");

        return NoContent();
    }
}