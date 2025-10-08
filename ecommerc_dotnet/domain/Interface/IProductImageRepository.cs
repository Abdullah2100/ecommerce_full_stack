using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.domain.Interface;

public interface IProductImageRepository: IRepository<ProductImage>
{
    Task<int> deleteProductImages(Guid id);
    Task<int> deleteProductImages(List<string> images, Guid id);
    Task<bool> addProductImage(ICollection<ProductImage> productImage);
    Task<List<string>> getProductImages(Guid id);
}