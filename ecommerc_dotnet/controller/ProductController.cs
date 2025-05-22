using System.Xml.Linq;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.midleware.ConfigImplment;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/Product")]
public class ProductController : ControllerBase
{
    public ProductController(
        IConfig configuration,
        IWebHostEnvironment host,
        IHubContext<EcommercHub> hubContext,
        AppDbContext appDbContext)
    {
        _userData = new UserData(appDbContext, configuration);
        _productData = new ProductData(appDbContext, configuration, host);
        _storeData = new StoreData(appDbContext, configuration);
        _configuration = configuration;
        _host = host;
        _hubContext = hubContext;
    }

    private readonly UserData _userData;
    private readonly ProductData _productData;
    private readonly StoreData _storeData;
    private readonly IConfig _configuration;
    private readonly IWebHostEnvironment _host;
    private readonly IHubContext<EcommercHub> _hubContext;

    [HttpGet("{store_id}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> getProducts
    (
        Guid store_id, int pageNumber
    )
    {
        var isExist = await _storeData.isExist(store_id);
        if (!isExist)
        {
            return BadRequest("المتجر غير موجود");
        }

        var result = await _productData.getProducts(
            store_id, pageNumber
        );


        return StatusCode(200, result);
    }


    [HttpGet("{store_id:guid}/{subCategory_id:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> getProducts
    (
        Guid store_id,
        Guid subCategory_id,
        int pageNumber
    )
    {
        var isExist = await _storeData.isExist(store_id, subCategory_id);
        if (!isExist)
        {
            return BadRequest("المتجر غير موجود");
        }

        var result = await _productData.getProducts(
            store_id,
            subCategory_id
            , pageNumber
        );


        return StatusCode(200, result);
    }


    [HttpGet("{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> getProducts
        (int pageNumber)
    {
        var result = await _productData.getProducts(pageNumber);


        return StatusCode(200, result);
    }


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> CreateProduct
    (
        [FromForm] ProductRequestDto product
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUser(userId);
        if (user == null)
            return BadRequest("المستخدم غير موجود");

        if (user.store_id != product.store_id)
            return BadRequest("فقط صاحب المتجر بأمكانه اضافة المنتجات الى متجره");

        var userStore = await _storeData.getStoreByUser((Guid)user.Id);

        if (userStore == null)
            return BadRequest("المتجر غير موجو");

        if (userStore.isBlocked)
            return BadRequest("لا يمكن اضافة اي منتج الى المتجر لان المتجر محضور تواصل مع مدير النظام");

        if (product.images.Count > 15)
            return BadRequest("اقصى عدد صور المنتجات هو 15 صورة");


        var savedThumbnail = await clsUtil.saveFile(product.thmbnail, clsUtil.enImageType.PRODUCT, _host);
        var savedImage = await clsUtil.saveFile(product.images, clsUtil.enImageType.PRODUCT, _host);
        if (savedImage == null || savedThumbnail == null)
        {
            return BadRequest("حدثة مشكلة اثناء حفظ الصور");
        }

        var result = await _productData.createProduct(
            name: product.name,
            description: product.description,
            thumbnail: (string)savedThumbnail!,
            subcategory_id: product.subcategory_id,
            store_id: product.store_id,
            price: product.price,
            productVarients: product.productVarients,
            images: savedImage
        );

        if (result == null)
            return BadRequest("حدثة مشكلة اثناء حفظ المنتج");

        return StatusCode(201, result);
    }

    [HttpPut("{store_id:guid}/{product_id:guid}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> updateProduct
    (
        [FromForm] ProductRequestUpdateDto product
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUser(userId);
        if (user == null)
            return BadRequest("المستخدم غير موجود");

        if (user.store_id != product.store_id)
            return BadRequest("فقط صاحب المتجر بأمكانه  المنتجات الى متجره");

        var userStore = await _storeData.getStoreByUser((Guid)user.Id);

        if (userStore == null)
            return BadRequest("المتجر غير موجو");

        if (userStore.isBlocked)
            return BadRequest("لا يمكن اضافة اي منتج الى المتجر لان المتجر محضور تواصل مع مدير النظام");

        var isExistProduct = await _productData.isExist(product.id);

        if (!isExistProduct)
            return BadRequest("المنتج غير موجو");

        if (product.images != null & product?.images?.Count > 15)
            return BadRequest("اقصى عدد صور المنتجات هو 15 صورة");

        if (product?.deletedProductVarients != null)
            await _productData.deleteProductVarient(product.deletedProductVarients, product.id);


        if (product?.deletedimages?.Count > 0)
            await _productData.deleteProductImages(product.deletedimages);

        string? savedThumbnail = null;
        if (product.thmbnail != null)
            savedThumbnail = await clsUtil.saveFile(product.thmbnail, clsUtil.enImageType.PRODUCT, _host);

        List<string>? savedImage = null;
        if (product.images != null)
            savedImage = await clsUtil.saveFile(product.images, clsUtil.enImageType.PRODUCT, _host);


        var result = await _productData.updateProduct(
            id: product.id,
            name: product.name,
            description: product.description,
            thumbnail: (string)savedThumbnail!,
            subcategory_id: product.subcategory_id,
            store_id: product.store_id,
            price: product.price,
            productVarients: product.productVarients,
            images: savedImage
        );

        if (result == null)
            return BadRequest("حدثة مشكلة اثناء حفظ المنتج");

        return StatusCode(200, result);
    }

    [HttpDelete("{store_id:guid}/{product_id:guid}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> deleteProduct
    (
        Guid store_id,
        Guid product_id
    )
    {
        var authorizationHeader = HttpContext.Request.Headers["Authorization"];
        var id = AuthinticationServices.GetPayloadFromToken("id",
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

        var user = await _userData.getUser(userId);
        if (user == null)
            return BadRequest("المستخدم غير موجود");

        if (user.store_id != store_id)
            return BadRequest("فقط صاحب المتجر بأمكانه حذف المنتجات ");

        var isExistProduct = await _productData.isExist(product_id);

        if (!isExistProduct)
            return BadRequest("المنتج غير موجو");


        var result = await _productData.deleteProduct(
            product_id: product_id
        );

        if (result == false)
            return BadRequest("حدثة مشكلة اثناء  حذف المنتج");

        return StatusCode(200, result);
    }
}