using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.domain.Interface;

public interface IProductImageRepository: IRepository<ProductImage>
{
   void deleteProductImages(Guid id);
    void deleteProductImages(List<string> images, Guid id);
    void  addProductImage(ICollection<ProductImage> productImage);
    Task<List<string>> getProductImages(Guid id);
}