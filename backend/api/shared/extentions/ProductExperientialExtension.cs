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
            Precentage = productVariant.Precentage,
            VariantId = productVariant.Id
        };
    }
    
    public static AdminProductVarientDto ToAdminProductVarientDto(this ProductVariant productVariant)
    {
        return new AdminProductVarientDto
        {
            Name = productVariant.Product.Name,
            Precentage = productVariant.Precentage,
            VariantName = productVariant.Varient.Name
            
        };
    }
    
}