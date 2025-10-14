using System.Security.Claims;
using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.Presentation.dto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.Presentation.controller;

[Authorize]
[ApiController]
[Route("api/Product")]
public class ProductController(
    IProductSerivces productServices,
    IAuthenticationService authenticationService
    ) : ControllerBase
{
    [HttpGet("store/{storeId}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getProducts
    (
        Guid storeId, int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await productServices.getProductsByStoreId(storeId,pageNumber,25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }


    [HttpGet("category/{categoryId}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getProductsByCategory
    (
        Guid categoryId, int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");
        var result = await productServices.getProductsByCategoryId(categoryId,pageNumber,25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }


    [HttpGet("{storeId:guid}/{subcategoryId:guid}/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getProducts
    (
        Guid storeId,
        Guid subcategoryId,
        int pageNumber
    )
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await productServices.getProducts(
            storeId,
            subcategoryId,
            pageNumber,
            25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }


    [HttpGet("all/{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getProducts
        (int pageNumber)
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        var result = await productServices.getProducts(pageNumber,25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }

  
    [HttpGet("{pageNumber:int}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> getProductsAdmin
        (int pageNumber)
    {
        if (pageNumber < 1)
            return BadRequest("رقم الصفحة لا بد ان تكون اكبر من الصفر");

        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid adminId = Guid.Empty;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            adminId = outId;
        }

        if (adminId == Guid.Empty)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await productServices.getProductsForAdmin(adminId,
            pageNumber,
            25);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }


  

    [HttpPost("")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> createProduct
    (
        [FromForm] CreateProductDto product
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
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

        var result = await productServices.createProducts(
            userId,product);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }



    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<IActionResult> updateProduct
    (
        [FromForm] UpdateProductDto product
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
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

        var result = await productServices.updateProducts(
            userId,product);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };  
    }

    [HttpDelete("{storeId:guid}/{productId:guid}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> deleteProduct
    (
        Guid storeId,
        Guid productId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.getPayloadFromToken("id",
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
        var result = await productServices.deleteProducts(
            userId,productId);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        }; 
    }
    
    
}