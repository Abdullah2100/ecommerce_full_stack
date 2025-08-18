using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using System.Collections.Generic;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.module;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class ProductServices(
    IConfig config,
    IWebHostEnvironment host,
    IProductRepository productRepository,
    IUserRepository userRepository,
    ISubCategoryRepository subCategoryRepository)
    : IProductSerivces
{
    public async Task<Result<List<ProductDto>>> getProductsByStoreId(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await productRepository
                .getProducts(storeId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> getProductsByCategoryId(
        Guid categryId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await productRepository
                .getProductsByCategory(categryId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> getProducts(
        Guid storeId,
        Guid subCategoryId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await productRepository
                .getProducts(storeId, subCategoryId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> getProducts(
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await productRepository
                .getAllAsync(pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<AdminProductsDto>>> getProductsForAdmin(
        Guid adminId,
        int pageNum,
        int pageSize
    )
    {
        User? user = await userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }


        List<AdminProductsDto> products = (await productRepository
                .getAllAsync(pageNum, pageSize))
            .Select((de) => de.toAdminDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<AdminProductsDto>>(
            data: products,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    private async Task<Result<ProductDto?>?> isUserNotExistOrNotHasStore(Guid userId)
    {
        User? user = await userRepository
            .getUser(userId);
        if (user is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user is blocked",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (user.Store is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not has store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (user.Store.IsBlock)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "admin is block you store from creating product",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return null;
    }

    public async Task<Result<ProductDto?>> createProducts(
        Guid userId,
        CreateProductDto productDto
    )
    {
        var isPassed = await isUserNotExistOrNotHasStore(userId);

        if (isPassed is not null)
        {
            return isPassed;
        }


        string? savedThumbnail = await clsUtil.saveFile(
            productDto.Thmbnail,
            EnImageType.PRODUCT,
            host);
        List<string>? savedImage = await clsUtil.saveFile(
            productDto.Images,
            EnImageType.PRODUCT,
            host);
        if (savedImage is null || savedThumbnail is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while saving image ",
                isSeccessful: false,
                statusCode: 400
            );
        }


        var id = clsUtil.generateGuid();

        List<ProductImage> images = savedImage.Select(pi => new ProductImage
            {
                Id = clsUtil.generateGuid(),
                Path = pi,
                ProductId = id
            })
            .ToList();

        if ((images.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSeccessful: false,
                statusCode: 404
            );
        }


        List<ProductVarient>? productVarients = null;
        if (productDto.ProductVarients is not null)
            productVarients = productDto
                .ProductVarients!.Select(pv =>
                    new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Name = pv.Name,
                        Precentage = pv.Precentage,
                        ProductId = id,
                        VarientId = pv.VarientId,
                        OrderProductsVarients = null
                    })
                .ToList();

        if (productVarients is not null && productVarients.Count > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "productvarient  can maximum has 20 images",
                isSeccessful: false,
                statusCode: 404
            );
        }

        var product = new Product
        {
            Id = id,
            Name = productDto.Name,
            Description = productDto.Description,
            SubcategoryId = productDto.SubcategoryId,
            StoreId = productDto.StoreId,
            Price = productDto.Price,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            Thmbnail = savedThumbnail,
            ProductImages = images,
            ProductVarients = productVarients
        };

        int result = await productRepository.addAsync(product);

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while adding product",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<ProductDto?>
        (
            data: product?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<ProductDto?>> updateProducts(
        Guid userId, UpdateProductDto productDto
    )
    {
        if (productDto.isEmpty())
            return new Result<ProductDto?>
            (
                data: null,
                message: "",
                isSeccessful: true,
                statusCode: 201
            );

        var isPassed = await isUserNotExistOrNotHasStore(userId);

        if (isPassed is not null)
        {
            return isPassed;
        }

        if (productDto.SubcategoryId is not null &&
            !(await subCategoryRepository.isExist((Guid)productDto!.SubcategoryId!)))
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "subCategory  is not found ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        Product? product = await productRepository.getProduct(productDto.Id, productDto.StoreId);

        if (product is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product is not found ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int result = 0;
        
        //delete preview images
        if (productDto.Deletedimages is not null)
        {
            result = await productRepository.deleteProductImages(productDto.Deletedimages, productDto.Id);
            if (result == 0)
            {
                return new Result<ProductDto?>
                (
                    data: null,
                    message: "unable to delete product images that store deleted",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }

        //delete preview productvarients
        if (productDto.DeletedProductVarients is not null)
        {
            result = await productRepository.deleteProductVarient(productDto.DeletedProductVarients, productDto.Id);
            if (result == 0)
            {
                return new Result<ProductDto?>
                (
                    data: null,
                    message: "unable to delete productvarients that store deleted",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }

        string? savedThumbnail = null;
        List<ProductImage>? savedImage = null;

        if (productDto.Thmbnail is not null)
            savedThumbnail = await clsUtil.saveFile(
                productDto.Thmbnail,
                EnImageType.PRODUCT,
                host);

        if (productDto.Images is not null)
            savedImage = (await clsUtil.saveFile(
                    productDto.Images,
                    EnImageType.PRODUCT,
                    host))
                ?.Select(im => new ProductImage
                {
                    Id = clsUtil.generateGuid(),
                    Path = im,
                    ProductId = product.Id
                }).ToList();

        if (savedImage is not null && (savedImage.Count + product?.ProductImages?.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSeccessful: false,
                statusCode: 404
            );
        }
        
        if ((savedImage?.Count + product?.ProductImages?.Count) < 1 )
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image must  has 2 image at least ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        List<ProductVarient>? productVarients = null;
        if (productDto.ProductVarients is not null)
            productVarients = productDto
                .ProductVarients!.Select(pv =>
                    new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Name = pv.Name,
                        Precentage = pv.Precentage,
                        ProductId = product!.Id,
                        VarientId = pv.VarientId,
                        OrderProductsVarients = null
                    })
                .ToList();
        if (productVarients is not null && (productVarients.Count + product?.ProductVarients?.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSeccessful: false,
                statusCode: 404
            );
        }

        //delete the previs images 


        product!.Name = productDto.Name ?? product.Name;
        product.Description = productDto.Description ?? product.Description;
        product.SubcategoryId = productDto.SubcategoryId ?? product.SubcategoryId;
        product.Price = productDto.Price ?? product.Price;
        product.UpdatedAt = DateTime.Now;
        product.Thmbnail = savedThumbnail ?? product.Thmbnail;
        product.ProductVarients = productVarients;
        product.ProductImages = savedImage;

        result = await productRepository.updateAsync(product);

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while updating product",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Product? finalUpdateProduct = await productRepository.getProduct(product.Id);

        return new Result<ProductDto?>
        (
            data: finalUpdateProduct?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteProducts(
        Guid userId,
        Guid id
    )
    {
        var isPassed = await isUserNotExistOrNotHasStore(userId);

        if (isPassed is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isPassed.Message,
                isSeccessful: false,
                statusCode: 404
            );
        }


        Product? product = await productRepository.getProduct(id, userId);

        if (product is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "product is not found ",
                isSeccessful: true,
                statusCode: 404
            );
        }

        int result = await productRepository.deleteAsync(product.Id);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "product had linke with some order",
                isSeccessful: true,
                statusCode: 400
            );
        }

        if (product.ProductImages is not null)
            foreach (var image in product.ProductImages)
            {
                clsUtil.deleteFile(image.Path, host);
            }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
}