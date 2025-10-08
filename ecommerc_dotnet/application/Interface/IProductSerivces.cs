using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface IProductSerivces
{
    Task<Result<List<ProductDto>>> getProductsByStoreId(Guid storeId,int pageNum,int pageSize);
    Task<Result<List<ProductDto>>> getProductsByCategoryId(Guid categryId,int pageNum,int pageSize);
    
    Task<Result<List<ProductDto>>> getProducts(Guid storeId,Guid subCategoryId,int pageNum,int pageSize);
    Task<Result<List<ProductDto>>> getProducts(int pageNum,int pageSize);
    Task<Result<List<AdminProductsDto>>> getProductsForAdmin(
        Guid adminId, int pageNum,int pageSize);
    
    Task<Result<ProductDto?>> createProducts(Guid userId,CreateProductDto productDto);
    Task<Result<ProductDto?>> updateProducts(Guid userId,UpdateProductDto productDto);
    Task<Result<bool>> deleteProducts(Guid userId,Guid id);
    
}