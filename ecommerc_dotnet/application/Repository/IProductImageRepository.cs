using ecommerc_dotnet.module;

namespace ecommerc_dotnet.application.Repository;

public interface IProductImageRepository
{
    Task<int> deleteProductImages(Guid id);
    Task<int> deleteProductImages(List<string> images, Guid id);
    Task<bool> addProductImage(ICollection<ProductImage> productImage);
    Task<List<string>> getProductImages(Guid id);
}