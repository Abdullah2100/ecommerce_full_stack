using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;

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

        var userHolder = await _userData.getUser(idHolder.Value);
        if (userHolder == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.store_id == null)
        {
            return NotFound("لا بد من انشاء متجر قبل اضافة اي لوحة اعلانية");
        }

        var imagePath = await clsUtil.saveFile(banner.image, clsUtil.enImageType.BANNER, _host);

        if (imagePath == null)
        {
            return BadRequest("حدثت مشكلة اثناء حفظ الصورة");
        }


        var result = await _bannerData.addNewBanner(banner.end_at, imagePath, (Guid)userHolder.store_id!);

        if (result == null)
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

        var userHolder = await _userData.getUser(idHolder.Value);
        if (userHolder == null)
        {
            return NotFound("المستخدم غير موجود");
        }

        if (userHolder.store_id == null)
        {
            return NotFound("ليس لديك اي متجر");
        }

        var banner = await _bannerData.getBanner((Guid)userHolder.store_id!, banner_id);

        if ((banner == null))
        {
            return BadRequest("اللوحة الاعلانية غير موجودة");
        }


        clsUtil.deleteFile(banner.image, _host);


        var result = await _bannerData.deleteBanner(banner.id);

        if (result == false)
            return BadRequest("حدثت مشكلة اثناء حذف الوحة الاعلانية");

        return NoContent();
    }


    [HttpGet("{store_Id:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBanner(
        Guid store_Id, int pageNumber
    )
    {
        var result = await _bannerData.getBanner(store_Id, pageNumber);

        if (result == null)
            return NoContent();
        return StatusCode(200, result);
    }
    

    [HttpGet("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getBannerRandom()
    {
        var result = await _bannerData.getBanner(15);
        if (result == null)
            return NoContent();
        
        return StatusCode(200, result);
    }
    
}