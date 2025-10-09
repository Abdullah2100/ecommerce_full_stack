using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.application.Repository;

public interface IProductVariantRepository:IRepository<ProductVarient>
{
    public Task<ProductVarient?> getProductVarient(Guid productId,Guid id);
    void addProductVariants(ICollection<ProductVarient> productVariants);
    void deleteProductVariantByProductId(Guid productId);
    void deleteProductVariant(List<CreateProductVarientDto> productVariants, Guid productId);
}
