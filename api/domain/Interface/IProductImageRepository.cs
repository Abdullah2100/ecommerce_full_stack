using api.domain.entity;

namespace api.domain.Interface;

public interface IProductImageRepository: IRepository<ProductImage>
{
   void DeleteProductImages(Guid id);
    void DeleteProductImages(List<string> images, Guid id);
    void  AddProductImage(ICollection<ProductImage> productImage);
    Task<List<string>> GetProductImages(Guid id);
}