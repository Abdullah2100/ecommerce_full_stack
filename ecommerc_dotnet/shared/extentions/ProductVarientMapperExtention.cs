using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.mapper;

public static class ProductVarientMapperExtention
{
    public static ProductVarientDto toProductVarientDto(this ProductVarient productVarient)
    {
        return new ProductVarientDto
        {
            ProductId = productVarient.ProductId,
            Name = productVarient.Name,
            Precentage = productVarient.Precentage,
            VarientId = productVarient.Id
        };
    }
    
    public static AdminProductVarientDto toAdminProductVarientDto(this ProductVarient productVarient)
    {
        return new AdminProductVarientDto
        {
            Name = productVarient.Product.Name,
            Precentage = productVarient.Precentage,
            VarientName = productVarient.Varient.Name
            
        };
    }
    
}