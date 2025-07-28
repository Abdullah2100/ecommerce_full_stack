using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class OrderProductMapperExtention
{
    public static OrderProductDto ToOrderProductDto(this OrderItem orderItem)
    {
        return new OrderProductDto
        {
            Id = orderItem.Product.Id,
            Name = orderItem.Product.Name

        };
    }
}