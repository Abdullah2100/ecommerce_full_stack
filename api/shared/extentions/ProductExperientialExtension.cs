using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.extentions;

public static class ProductExperientialExtension
{
    public static ProductVariantDto ToProductVarientDto(this ProductVariant productVariant)
    {
        return new ProductVariantDto
        {
            ProductId = productVariant.ProductId,
            Name = productVariant.Name,
            Percentage = productVariant.Percentage,
            VariantId = productVariant.Id
        };
    }
    
    public static AdminProductVarientDto ToAdminProductVarientDto(this ProductVariant productVariant)
    {
        return new AdminProductVarientDto
        {
            Name = productVariant.Product.Name,
            Percentage = productVariant.Percentage,
            VariantName = productVariant.Variant.Name
            
        };
    }
    
}