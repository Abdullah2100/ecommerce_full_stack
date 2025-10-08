using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

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