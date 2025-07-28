using System.Security.Claims;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
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
[Route("api/Store")]
public class StoreController : ControllerBase
{
    public StoreController(AppDbContext dbContext
        , IConfig configuration,
        IWebHostEnvironment webHostEnvironment,
        IHubContext<EcommerceHub> hubContext,
        IUnitOfWork unitOfWork
    )
    {
        _host = webHostEnvironment;
        _storeData = new StoreData(dbContext, configuration,unitOfWork);
        _userService = new UserService(dbContext, configuration,unitOfWork);
        _configuration = configuration;
        _hubContext = hubContext;
    }

    private readonly StoreData _storeData;
    private readonly UserService _userService;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;
    private readonly IHubContext<EcommerceHub> _hubContext;

    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> CreateNewStore(
        [FromForm] CreateStoreDto store)
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

        if (user.IsBlocked
        //|| (user.id !=  store.userId && store.userId !=  null && user.role == 1)
        )
            return BadRequest("ليس لديك الصلاحية لانشاء متجر جديد");

        bool isExist = await _storeData.isExist(store.Name);

        if (isExist)
            return Conflict("اسم التمتجر تم استخدامه يمكنك استخدام اسم اخر");

        string? wallperper = null, smallImage = null;

        smallImage = await clsUtil.saveFile(store.SmallImage, EnImageType.STORE, _host);
        wallperper = await clsUtil.saveFile(store.WallpaperImage, EnImageType.STORE, _host);

        if (smallImage is null || wallperper is null)
            return BadRequest("حدثة مشكلة اثناء حفظ الصور");

        StoreDto? result = await _storeData.createStore(
            name: store.Name,
            wallpaperImage: wallperper,
            smallImage: smallImage,
            userId: idHolder.Value !=  store?.UserId && store?.UserId !=  null ? (Guid)store.UserId : user.Id,
            latitude: store.Latitude,
            longitude: store.Longitude
        );

        if (result is null)
        {
            if(wallperper is not null)
            clsUtil.deleteFile(wallperper ?? "", _host);
            if(smallImage is not null)
            clsUtil.deleteFile(smallImage ?? "", _host);
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");
        }

        return StatusCode(201, result);
    }


    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> updateStore(
        [FromForm] UpdateStoreDto store)
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

        if (store.isEmpty()) return Ok("ليس هناك اي تعديل");

        User? user = await _userService.getUser(idHolder.Value);
        if (user is null)
            return NotFound("المستخدم غير موجود");


        if (user.Store is null)
            return NotFound("المتجر غير موجود");

        if (user.Store.Name !=  store.Name && await _storeData.isExist(store.Name))
        {
            return Conflict("اسم المتجر تم استخدامه اختر اسما اخر");
        }

        if (store.WallpaperImage !=  null)
            clsUtil.deleteFile(user.Store.WallpaperImage, _host);

        if (store.SmallImage !=  null)
            clsUtil.deleteFile(user.Store.SmallImage, _host);


        string? wallperper = null, smallImage = null;

        if (store.SmallImage !=  null)
            smallImage = await clsUtil.saveFile(store.SmallImage, EnImageType.STORE, _host);
        if (store.WallpaperImage !=  null)
            wallperper = await clsUtil.saveFile(store.WallpaperImage, EnImageType.STORE, _host);


        StoreDto? result = await _storeData.updateStore(
            name: store.Name,
            wallpaperImage: wallperper,
            smallImage: smallImage,
            userId: user.Id,
            latitude: store.Latitude,
            longitude: store.Longitude
        );

        if (result is null)
        {
            if(wallperper is not null)
                clsUtil.deleteFile(wallperper ?? "", _host);
            if(smallImage is not null)
                clsUtil.deleteFile(smallImage ?? "", _host);   
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");
        }

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
            return BadRequest("المستخدم ليس لديه الصلاحية");

        bool isExist = await _storeData.isExist(storeId);

        if (!isExist)
            return NotFound("المتجر غير موجود");


        bool? result = await _storeData.updateStoreStatus(
            storeId
        );

        if (result==null)
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");
        
        await _hubContext.Clients.All.SendAsync("storeStatus", new StoreStatusDto 
        {
            StoreId = storeId,
            Status = (bool)result
        });

        return NoContent();
    }


    [HttpGet("me")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getStore()
    {
          StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationServices.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
    
        Guid userId = Guid.Empty;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            userId = outId;
        }

        if (userId == Guid.Empty)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        StoreDto? result = await _storeData.getStoreByUser(userId);

        if (result is null)
            return NoContent();
        return Ok(result);
    }


    [HttpGet("{storeId:guid}")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getStore(Guid storeId)
    {
        StoreDto? result = await _storeData.getStoreById(storeId);

        if (result is null)
            return NotFound("المتجر غير موجود");
        return Ok(result);
    }


    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> getStorePages()
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

        User? userData = await _userService.getUser(idHolder.Value);

        if (userData is null || userData.Role !=  0)
            return BadRequest("ليس لديك الصلاحية للوصول الى البيانات");

        int pages = await _storeData.getStorePages();
        return Ok(pages);
    }

    [HttpGet("{page:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetStores(int page = 1)
    {
        List<StoreDto>? result = await _storeData.getStore(page);

        if (result is null)
            return NoContent();
        return Ok(result);
    }


    [HttpGet("address/{storeId:guid}/{page:int}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetStoreAddress(Guid storeId, int page)
    {
        List<AddressDto>? result = await _storeData.getStoreAddress(storeId, page);

        if (result.Count < 1)
            return NotFound("المتجر غير موجود");
        return Ok(result);
    }
}