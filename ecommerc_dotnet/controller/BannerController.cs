using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
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
        IHubContext<EcommercHub> hubContext,
        AppDbContext appDbContext)
    {
        _bannerData = new BannerData(appDbContext, configuration, host);
        _userData = new UserData(appDbContext, configuration);
        _configuration = configuration;
        _host = host;
        _hubContext = hubContext;
    }

    private readonly BannerData _bannerData;
    private readonly UserData _userData;
    private readonly AppDbContext _dbContext;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;
    private readonly IHubContext<EcommercHub> _hubContext;


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createBanner(
        [FromForm] BannerRequestDto banner
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

        UserInfoResponseDto? userHolder = await _userData.getUser(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.storeId is null)
        {
            return NotFound("لا بد من انشاء متجر قبل اضافة اي لوحة اعلانية");
        }

        string? imagePath = await clsUtil.saveFile(banner.image, clsUtil.enImageType.BANNER, _host);

        if (imagePath is null)
        {
            return BadRequest("حدثت مشكلة اثناء حفظ الصورة");
        }


        BannerResponseDto? result = await _bannerData.addNewBanner(banner.endAt, imagePath, (Guid)userHolder.storeId!);

        if (result is null)
            return BadRequest("حدثت مشكلة اثناء حقظ الوحة الاعلانية");

        await _hubContext.Clients.All.SendAsync("createdBanner", result);
        return StatusCode(201, result);
    }


    [HttpDelete("{banner_id:guid}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteBanner(
        Guid banner_id
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

        UserInfoResponseDto? userHolder = await _userData.getUser(idHolder.Value);
        if (userHolder is null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.storeId is null)
        {
            return NotFound("ليس لديك اي متجر");
        }

        BannerResponseDto? banner = await _bannerData.getBanner((Guid)userHolder.storeId!, banner_id);

        if ((banner is null))
        {
            return BadRequest("اللوحة الاعلانية غير موجودة");
        }


        clsUtil.deleteFile(banner.image, _host);


        bool result = await _bannerData.deleteBanner(banner.id);

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

        List<BannerResponseDto>? result = await _bannerData.getBanner(storeId, pageNumber);

        if (result is null)
            return NoContent();
        return StatusCode(200, result);
    }
    

    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBannerRandom()
    {
        var result = await _bannerData.getBanner(15);
        if (result is null)
            return NoContent();
        
        return StatusCode(200, result);
    }
    
}