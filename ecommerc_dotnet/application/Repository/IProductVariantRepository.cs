using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.application.Repository;

public interface IProductVariantRepository
{
    public Task<ProductVarient?> getProductVarient(Guid productId,Guid id);
    Task<bool> addProductVariants(ICollection<ProductVarient> productVariants);
    Task<int>deleteProductVariantByProductId(Guid productId);
    Task<int>deleteProductVariant(List<CreateProductVarientDto> productVariants, Guid productId);
}
