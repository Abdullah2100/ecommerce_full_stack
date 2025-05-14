using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Store")]
public class StoreController : ControllerBase
{
    public StoreController(AppDbContext dbContext
        , IConfigurationServices configuration,
        IWebHostEnvironment webHostEnvironment
    )
    {
        _host = webHostEnvironment;
        _storeData = new StoreData(dbContext, configuration);
        _userData = new UserData(dbContext, configuration);
        _configuration = configuration;
    }

    private readonly StoreData _storeData;
    private readonly UserData _userData;
    private readonly IConfigurationServices _configuration;
    private readonly IWebHostEnvironment _host;

    [HttpPost("new")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> CreateNewStore([FromForm] StoreRequestDto store)
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

        var user = await _userData.getUserById(idHolder.Value);
        if (user == null)
            return BadRequest("المستخدم غير موجود");
        
        if(user.ID != store.user_id&&user.role==1)
            return BadRequest("ليس لديك الصلاحية لانشاء متجر جديد");

        bool isExist = await _storeData.isExist(store.name);

        if (isExist)
            return BadRequest("اسم التمتجر تم استخدامه يمكنك استخدام اسم اخر");

        string? wallperper=null  , small_image = null;

        small_image =await clsUtil.saveFile(store.small_image, clsUtil.enImageType.STORE, _host);
        wallperper =await clsUtil.saveFile(store.wallpaper_image, clsUtil.enImageType.STORE, _host);

        if (small_image == null || wallperper == null)
            return BadRequest("حدثة مشكلة اثناء حفظ الصور");

        var result =await _storeData.createStore(
            name: store.name,
            wallpaper_image: wallperper,
            small_image: small_image,
            user_id: user.ID,
            latitude: store.latitude,
            longitude: store.longitude
        );

        if (result == null)
            return BadRequest("حدثت مشكلة اثناء حفظ البيانات");

        return StatusCode(201, result);

    }
    
    
    
    
    
    [HttpGet("{userId:guid}")]
    public IActionResult GetStore(Guid userId)
    {
        var result = _storeData.getStore(userId);
        
        if(result == null)
            return StatusCode(400,"المتجر غير موجود");
        return Ok(result);
        
    }
    
}