using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
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
[Route("api/Store")]
public class StoreController : ControllerBase
{
    public StoreController(AppDbContext dbContext
        , IConfig configuration,
        IWebHostEnvironment webHostEnvironment,
        IHubContext<EcommercHub> hubContext
    )
    {
        _host = webHostEnvironment;
        _storeData = new StoreData(dbContext, configuration);
        _userData = new UserData(dbContext, configuration);
        _configuration = configuration;
        _hubContext = hubContext;
    }

    private readonly StoreData _storeData;
    private readonly UserData _userData;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;
    private readonly IHubContext<EcommercHub> _hubContext;

    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> CreateNewStore(
        [FromForm] StoreRequestDto store)
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

        if (user.isDeleted
        //|| (user.id != store.userId && store.userId != null && user.role == 1)
        )
            return BadRequest("ليس لديك الصلاحية لانشاء متجر جديد");

        bool isExist = await _storeData.isExist(store.name);

        if (isExist)
            return Conflict("اسم التمتجر تم استخدامه يمكنك استخدام اسم اخر");

        string? wallperper = null, smallImage = null;

        smallImage = await clsUtil.saveFile(store.smallImage, clsUtil.enImageType.STORE, _host);
        wallperper = await clsUtil.saveFile(store.wallpaperImage, clsUtil.enImageType.STORE, _host);

        if (smallImage == null || wallperper == null)
            return BadRequest("حدثة مشكلة اثناء حفظ الصور");

        StoreResponseDto? result = await _storeData.createStore(
            name: store.name,
            wallpaperImage: wallperper,
            smallImage: smallImage,
            userId: idHolder.Value != store?.userId && store.userId != null ? (Guid)store.userId : user.id,
            latitude: store.latitude,
            longitude: store.longitude
        );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");

        return StatusCode(201, result);
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> updateStore(
        [FromForm] StoreUpdateDto store)
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


        if (user.store == null)
            return NotFound("المتجر غير موجود");

        if (user.store.name != store.name && await _storeData.isExist(store.name))
        {
            return Conflict("اسم المتجر تم استخدامه اختر اسما اخر");
        }

        if (store.wallpaperImage != null)
            clsUtil.deleteFile(user.store.wallpaperImage, _host);

        if (store.smallImage != null)
            clsUtil.deleteFile(user.store.smallImage, _host);


        string? wallperper = null, smallImage = null;

        if (store.smallImage != null)
            smallImage = await clsUtil.saveFile(store.smallImage, clsUtil.enImageType.STORE, _host);
        if (store.wallpaperImage != null)
            wallperper = await clsUtil.saveFile(store.wallpaperImage, clsUtil.enImageType.STORE, _host);


        StoreResponseDto? result = await _storeData.updateStore(
            name: store.name,
            wallpaperImage: wallperper,
            smallImage: smallImage,
            userId: user.id,
            latitude: store.latitude,
            longitude: store.longitude
        );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");

        return StatusCode(200, result);
    }


    [HttpPut("status/{storeId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateStoreStatus(
        Guid storeId
    )
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
            return BadRequest("المستخدم ليس لديه الصلاحية");

        bool isExist = await _storeData.isExist(storeId);

        if (!isExist)
            return NotFound("المتجر غير موجود");


        bool? result = await _storeData.updateStoreStatus(
            storeId
        );

        if (result==null)
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");
        
        await _hubContext.Clients.All.SendAsync("storeStatus", new StoreStatusResponseDto
        {
            storeId = storeId,
            status = (bool)result
        });

        return NoContent();
    }


    [HttpGet("me")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> GetStore()
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid userId = Guid.Empty;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outID))
        {
            userId = outID;
        }

        if (userId == Guid.Empty)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        StoreResponseDto? result = await _storeData.getStoreByUser(userId);

        if (result == null)
            return NoContent();
        return Ok(result);
    }


    [HttpGet("{storeId:guid}")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetStore(Guid storeId)
    {
        StoreResponseDto? result = await _storeData.getStoreById(storeId);

        if (result == null)
            return NotFound("المتجر غير موجود");
        return Ok(result);
    }


    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> GetStorePages()
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

        User? userData = await _userData.getUserById(idHolder.Value);

        if (userData == null || userData.role != 0)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        int pages = await _storeData.getStorePages();
        return Ok(pages);
    }

    [HttpGet("{page:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetStores(int page = 1)
    {
        List<StoreResponseDto>? result = await _storeData.getStore(page);

        if (result == null)
            return NoContent();
        return Ok(result);
    }


    [HttpGet("address/{storeId:guid}/{page:int}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetStoreAddress(Guid storeId, int page)
    {
        List<AddressResponseDto>? result = await _storeData.getStoreAddress(storeId, page);

        if (result.Count < 1)
            return NotFound("المتجر غير موجود");
        return Ok(result);
    }
}