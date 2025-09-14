using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IProductRepository:IRepository<Product>
{
    
    Task<Product?> getProduct(Guid id);
    Task<Product?> getProduct(Guid id, Guid storeId);
    Task<Product?> getProductByUser(Guid id,Guid userId);
    
    Task<IEnumerable<Product>> getProducts(Guid storeId,Guid subCategoryId,int pageNum,int pageSize);
    Task<IEnumerable<Product>> getProducts(Guid storeId,int pageNum,int pageSize);
    Task<IEnumerable<Product>> getProductsByCategory(Guid categoryId,int pageNum,int pageSize);
    
    Task<bool> isExist(Guid id);
    
}