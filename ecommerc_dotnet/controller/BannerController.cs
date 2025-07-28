using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.services;
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
[Route("api/Banner")]
public class BannerController : ControllerBase
{
    public BannerController(
        IConfig configuration,
        IWebHostEnvironment host,
        IHubContext<EcommerceHub> hubContext,
        AppDbContext appDbContext,
        IUnitOfWork unitOfWork
        )
    {
        _bannerData = new BannerData(
            appDbContext, 
            configuration, 
            host,
            unitOfWork );
        _userService = new UserService(
            appDbContext, 
            configuration,
            unitOfWork);
        _configuration = configuration;
        _host = host;
        _hubContext = hubContext;
    }

    private readonly BannerData _bannerData;
    private readonly UserService _userService;
    private readonly AppDbContext _dbContext;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;
    private readonly IHubContext<EcommerceHub> _hubContext;


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createBanner(
        [FromForm] CreateBannerDto banner
    )
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

        User? userHolder = await _userService.getUser(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.Store is null)
        {
            return NotFound("لا بد من انشاء متجر قبل اضافة اي لوحة اعلانية");
        }

        string? imagePath = await clsUtil.saveFile(banner.Image, EnImageType.BANNER, _host);

        if (imagePath is null)
        {
            return BadRequest("حدثت مشكلة اثناء حفظ الصورة");
        }


        BannerDto? result = await _bannerData
            .addNewBanner(banner.EndAt, imagePath, (Guid)userHolder.Store.Id!);

        if (result is null)
        {
            clsUtil.deleteFile(imagePath, _host);
            return BadRequest("حدثت مشكلة اثناء حقظ الوحة الاعلانية");
        }

        await _hubContext.Clients.All.SendAsync("createdBanner", result);
        return StatusCode(201, result);
    }


    [HttpDelete("{bannerId:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteBanner(
        Guid bannerId
    )
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

        User? userHolder = await _userService.getUser(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.Store is null)
        {
            return NotFound("ليس لديك اي متجر");
        }

        BannerDto? banner = await _bannerData.getBanner((Guid)userHolder.Store.Id!, bannerId);

        if ((banner is null))
        {
            return BadRequest("اللوحة الاعلانية غير موجودة");
        }


        clsUtil.deleteFile(banner.Image, _host);


        bool result = await _bannerData.deleteBanner(banner.Id);

        if (result == false)
            return BadRequest("حدثت مشكلة اثناء حذف الوحة الاعلانية");

        return NoContent();
    }


    [HttpGet("{storeId:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBanner(
        Guid storeId, int pageNumber
    )
    {
        if(pageNumber<1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        List<BannerDto>? result = await _bannerData.getBanners(storeId, pageNumber);

        if (result is null)
            return NoContent();
        return StatusCode(200, result);
    }
    

    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBannerRandom()
    {
        var result = await _bannerData.getRandomBanners(15);
        if (result is null)
            return NoContent();
        
        return StatusCode(200, result);
    }
    
}