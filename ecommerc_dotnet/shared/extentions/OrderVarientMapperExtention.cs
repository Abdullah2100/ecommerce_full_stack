using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;

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