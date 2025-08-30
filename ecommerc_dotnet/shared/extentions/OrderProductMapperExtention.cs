using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.shared.extentions;

public static class OrderProductMapperExtention
{
    public static OrderProductDto ToOrderProductDto(this OrderItem orderItem,string url)
    {
        return new OrderProductDto
        {
            Id = orderItem.ProductId,
            Name = orderItem.Product.Name,
            Thmbnail=url+orderItem.Product.Thmbnail

        };
    }
}