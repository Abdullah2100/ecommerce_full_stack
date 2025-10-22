using ecommerc_dotnet.Presentation.dto.Response;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.mapper;

public static class OrderVarientMapperExtention
{
    public static OrderVarientDto toOrderVarientDto(this OrderProductsVarient orderProductsVarient)
    {
        return new OrderVarientDto
        {
            Name = orderProductsVarient.ProductVarient.Product.Name,
            VarientName = orderProductsVarient.ProductVarient.Varient.Name,

        };
    }
    
}