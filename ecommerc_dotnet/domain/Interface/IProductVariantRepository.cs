using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.application.Repository;

public interface IProductVariantRepository:IRepository<ProductVarient>
{
    public Task<ProductVarient?> getProductVarient(Guid productId,Guid id);
    Task<bool> addProductVariants(ICollection<ProductVarient> productVariants);
    Task<int>deleteProductVariantByProductId(Guid productId);
    Task<int>deleteProductVariant(List<CreateProductVarientDto> productVariants, Guid productId);
}
