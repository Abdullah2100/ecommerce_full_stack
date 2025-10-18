using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using System.Collections.Generic;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public class ProductServices(
    IConfig config,
    IUnitOfWork unitOfWork,
    IFileServices fileServices
)
    : IProductSerivces
{
    public async Task<Result<List<ProductDto>>> getProductsByStoreId(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .getProducts(storeId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> getProductsByCategoryId(
        Guid categryId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .getProductsByCategory(categryId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
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
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .getProducts(storeId, subCategoryId, pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> getProducts(
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .getProducts(pageNum, pageSize))
            .Select((de) => de.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<AdminProductsDto>>> getProductsForAdmin(
        Guid adminId,
        int pageNum,
        int pageSize
    )
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "not authorized user",
                isSuccessful: false,
                statusCode: 400
            );
        }


        List<AdminProductsDto> products = (await unitOfWork.ProductRepository
                .getProducts(pageNum, pageSize))
            .Select((de) => de.toAdminDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<AdminProductsDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    private async Task<Result<ProductDto?>?> isUserNotExistOrNotHasStore(Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(userId);
        if (user is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user is blocked",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (user.Store is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not has store",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (user.Store.IsBlock)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "admin is block you store from creating product",
                isSuccessful: false,
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


        string? savedThumbnail = await fileServices.saveFile(
            productDto.Thmbnail,
            EnImageType.PRODUCT);
        List<string>? savedImage = await fileServices.saveFile(
            productDto.Images,
            EnImageType.PRODUCT);
        if (savedImage is null || savedThumbnail is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while saving image ",
                isSuccessful: false,
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
                isSuccessful: false,
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
                isSuccessful: false,
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
            //ProductImages = images,
            // ProductVarients = productVarients
        };

        unitOfWork.ProductRepository.add(product);
        unitOfWork.ProductImageRepository.addProductImage(images);
        if (productVarients is not null)
            unitOfWork.ProductVariantRepository.addProductVariants(productVarients);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while adding product",
                isSuccessful: false,
                statusCode: 400
            );
        }

        product = await unitOfWork.ProductRepository.getProduct(product.Id);

        return new Result<ProductDto?>
        (
            data: product?.toDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
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
                isSuccessful: true,
                statusCode: 201
            );

        var isPassed = await isUserNotExistOrNotHasStore(userId);

        if (isPassed is not null)
        {
            return isPassed;
        }

        if (productDto.SubcategoryId is not null &&
            !(await unitOfWork.SubCategoryRepository.isExist((Guid)productDto!.SubcategoryId!)))
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "subCategory  is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        Product? product = await unitOfWork.ProductRepository.getProduct(productDto.Id, productDto.StoreId);

        if (product is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        int result = 0;

        //delete preview images
        if (productDto.Deletedimages is not null)
        {
              unitOfWork.ProductImageRepository.deleteProductImages(productDto.Deletedimages,
                productDto.Id);

            fileServices.deleteFile(productDto.Deletedimages);
        }

        //delete preview productvarients
        if (productDto.DeletedProductVarients is not null)
        {
             unitOfWork.ProductVariantRepository.deleteProductVariant(productDto.DeletedProductVarients,
                productDto.Id);
            
        }

        string? savedThumbnail = null;
        List<ProductImage>? savedImage = null;

        if (productDto.Thmbnail is not null)
            savedThumbnail = await fileServices.saveFile(
                productDto.Thmbnail,
                EnImageType.PRODUCT);

        if (productDto.Images is not null)
            savedImage = (await fileServices.saveFile(
                    productDto.Images,
                    EnImageType.PRODUCT)
                )
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
                isSuccessful: false,
                statusCode: 404
            );
        }

        if ((savedImage?.Count + product?.ProductImages?.Count) < 1)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image must  has 2 image at least ",
                isSuccessful: false,
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
                isSuccessful: false,
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

        unitOfWork.ProductRepository.update(product);
        result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while updating product",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Product? finalUpdateProduct = await unitOfWork.ProductRepository.getProduct(product.Id);

        return new Result<ProductDto?>
        (
            data: finalUpdateProduct?.toDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteProducts(
        Guid userId,
        Guid id
    )
    {
        var user = await unitOfWork.UserRepository.getUser(userId);
        var isPassed = await isUserNotExistOrNotHasStore(userId);

        if (isPassed is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isPassed.Message,
                isSuccessful: false,
                statusCode: 404
            );
        }


        Product? product = await unitOfWork.ProductRepository.getProduct(id, user.Store.Id);

        if (product is null || id != product.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "product is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.ProductRepository.delete(product.Id);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "product had linke with some order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (product.ProductImages is not null)
            foreach (var image in product.ProductImages)
            {
                fileServices.deleteFile(image.Path);
            }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }
}