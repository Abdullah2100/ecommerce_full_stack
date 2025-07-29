using System.Security.Claims;
using System.Xml.Linq;
using ecommerc_dotnet.context;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
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
[Route("api/Product")]
public class ProductController : ControllerBase
{
    public ProductController(
        IConfig configuration,
        IWebHostEnvironment host,
        IHubContext<EcommerceHub> hubContext,
        AppDbContext appDbContext,
        IUnitOfWork unitOfWork
    )
    {
        _userService = new UserService(appDbContext, configuration, unitOfWork);
        _productData = new ProductData(appDbContext, configuration, host, unitOfWork);
        _storeData = new StoreData(appDbContext, configuration, unitOfWork);
        _host = host;
    }

    private readonly UserService _userService;
    private readonly ProductData _productData;
    private readonly StoreData _storeData;
    private readonly IWebHostEnvironment _host;

    [HttpGet("store/{storeId}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> getProducts
    (
        Guid storeId, int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var isExist = await _storeData.isExist(storeId);
        if (!isExist)
        {
            return BadRequest("المتجر غير موجود");
        }

        var result = await _productData.getProducts(
            storeId, pageNumber
        );

        if (result.Count < 1)
            return NoContent();


        return StatusCode(200, result);
    }


    [HttpGet("category/{category_id}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> getProductsByCategory
    (
        Guid category_id, int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await _productData.getProductsByCategory(
            category_id, pageNumber
        );

        if (result is null)
            return NoContent();


        return StatusCode(200, result);
    }


    [HttpGet("{storeId:guid}/{subcategoryId:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getProducts
    (
        Guid storeId,
        Guid subcategoryId,
        int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var isExist = await _storeData.isExist(storeId, subcategoryId);
        if (!isExist)
        {
            return BadRequest("المتجر غير موجود");
        }

        var result = await _productData.getProducts(
            storeId,
            subcategoryId
            , pageNumber
        );

        if (result.Count < 1)
        {
            return NoContent();
        }

        return StatusCode(200, result);
    }


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getProducts
        (int pageNumber)
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await _productData.getProducts(pageNumber);

        if (result.Count == 0)
            return NoContent();

        return StatusCode(200, result);
    }

    [HttpGet("{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> getProductsAdmin
        (int pageNumber)
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        User? user = await _userService.getUser(userId);
        if (user is null)
            return NotFound("المستخدم غير موجود");
        if (user.Role == 1)
            return BadRequest("ليس لديك الصلاحية");
        var result = await _productData.getProductsAdmin(pageNumber);

        if (result.Count == 0)
            return NoContent();

        return StatusCode(200, result);
    }


    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> getProductsPagess()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        User? user = await _userService.getUser(userId);
        if (user is null)
            return NotFound("المستخدم غير موجود");
        if (user.Role == 1)
            return BadRequest("ليس لديك الصلاحية");

        var result = await _productData.getProduct();


        return StatusCode(200, result);
    }


    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> CreateProduct
    (
        [FromForm] CreateProductDto product
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        var user = await _userService.getUser(userId);
        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Store is not null &&user.Store.Id != product.StoreId)
            return BadRequest("فقط صاحب المتجر بأمكانه اضافة المنتجات الى متجره");

        var userStore = await _storeData.getStoreByUser((Guid)user.Id);

        if (userStore is null)
            return NotFound("المتجر غير موجو");

        if (userStore.IsBlocked)
            return BadRequest("لا يمكن اضافة اي منتج الى المتجر لان المتجر محضور تواصل مع مدير النظام");

        if (product.Images.Count > 15)
            return BadRequest("اقصى عدد صور المنتجات هو 15 صورة");


        var savedThumbnail = await clsUtil.saveFile(product.Thmbnail, EnImageType.PRODUCT, _host);
        var savedImage = await clsUtil.saveFile(product.Images, EnImageType.PRODUCT, _host);
        if (savedImage is null || savedThumbnail is null)
        {
            return BadRequest("حدثة مشكلة اثناء حفظ الصور");
        }

        var result = await _productData.createProduct(
            name: product.Name,
            description: product.Description,
            thumbnail: (string)savedThumbnail!,
            subcategoryId: product.SubcategoryId,
            storeId: product.StoreId,
            price: product.Price,
            images: savedImage,
            productVarients: product.ProductVarients
        );

        if (result is null)
        {
            if (product?.Thmbnail is null)
                clsUtil.deleteFile(savedThumbnail ?? "", _host);
            if (product?.Images.Count > 0)
                clsUtil.deleteFile(savedImage, _host);
            return BadRequest("حدثة مشكلة اثناء حفظ المنتج");
        }

        return StatusCode(201, result);
    }



    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> updateProduct
    (
        [FromForm] UpdateProductDto product
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        if (product.isEmpty()) return Ok("ليس هناك اي شئ يحتاج التحديث");
        
        var user = await _userService.getUser(userId);
        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Store is not null&& user.Store.Id != product.StoreId)
            return Conflict("فقط صاحب المتجر بأمكانه  المنتجات الى متجره");

        var userStore = await _storeData.getStoreByUser((Guid)user.Id);

        if (userStore is null)
            return NotFound("المتجر غير موجو");

        if (userStore.IsBlocked)
            return BadRequest("لا يمكن اضافة اي منتج الى المتجر لان المتجر محضور تواصل مع مدير النظام");

        var isExistProduct = await _productData.isExist(product.Id);

        if (!isExistProduct)
            return NotFound("المنتج غير موجو");

        if (product.Images != null & product?.Images?.Count > 15)
            return BadRequest("اقصى عدد صور المنتجات هو 15 صورة");

        if (product?.DeletedProductVarients != null)
            await _productData.deleteProductVarient(product.DeletedProductVarients, product.Id);


        if (product?.Deletedimages?.Count > 0)
            _productData.deleteProductImages(product.Deletedimages);

        string? savedThumbnail = null;
        if (product?.Thmbnail != null)
            savedThumbnail = await clsUtil.saveFile(product.Thmbnail, EnImageType.PRODUCT, _host);

        List<string>? savedImage = null;
        if (product?.Images != null)
            savedImage = await clsUtil.saveFile(product.Images, EnImageType.PRODUCT, _host);


        var result = await _productData.updateProduct(
            id: product.Id,
            name: product.Name,
            description: product.Description,
            thumbnail: (string)savedThumbnail!,
            subcategoryId: product.SubcategoryId,
            price: product.Price,
            productVarients: product.ProductVarients,
            images: savedImage
        );

        if (result is null)
        {
            if (product?.Thmbnail is null)
                clsUtil.deleteFile(savedThumbnail ?? "", _host);
            if (product?.Images.Count > 0)
                clsUtil.deleteFile(savedImage, _host);
            return BadRequest("حدثة مشكلة اثناء حفظ المنتج");
        }

        return StatusCode(200, result);
    }

    [HttpDelete("{storeId:guid}/{productId:guid}")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> deleteProduct
    (
        Guid storeId,
        Guid productId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
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

        var user = await _userService.getUser(userId);
        if (user is null)
            return NotFound("المستخدم غير موجود");

        if (user.Store is not null &&user.Store.Id != storeId)
            return Conflict("فقط صاحب المتجر بأمكانه حذف المنتجات ");

        var isExistProduct = await _productData.isExist(productId);

        if (!isExistProduct)
            return NotFound("المنتج غير موجو");


        var result = await _productData.deleteProduct(
            productId: productId
        );

        if (result == false)
            return BadRequest("حدثة مشكلة اثناء  حذف المنتج");

        return NoContent();
    }
}