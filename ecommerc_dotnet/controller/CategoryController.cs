using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Response;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Category")]
public class CategoryController : ControllerBase
{
    public CategoryController(AppDbContext dbContext
        , IConfigurationServices configuration,
        // IEmailServices emailServices,
        IWebHostEnvironment webHostEnvironment
    )
    {
        _host = webHostEnvironment;
    }

    private readonly CategoryData _categoryData;

    private readonly IWebHostEnvironment _host;


    [HttpGet("{pageNumber:int}")]
    public IActionResult getCatgory(int pageNumber = 1)
    {
        if(pageNumber < 1)
            return BadRequest("خطء في البيانات المرسلة");

        var categories = _categoryData.getCategories(pageNumber);
        if (categories == null)
            return BadRequest("لا يوجد اي اقسام");
        return Ok(categories);
        
    }
    
}