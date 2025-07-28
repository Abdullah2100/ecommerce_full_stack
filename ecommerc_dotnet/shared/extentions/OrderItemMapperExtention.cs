using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class OrderItemMapperExtention
{
    public static OrderItemDto toOrderItemDto(this OrderItem item)
    {
        return new OrderItemDto
        {
            Id = item.Id,
            OrderId = item.OrderId,
            OrderItemStatus = item.Status.ToString(),
            Price = item.Price,
            Product = item.ToOrderProductDto(),
            Quanity = item?.Quanity ?? 0,
            ProductVarient = item
                ?.OrderProductsVarients
                ?.Select(ost => ost.toOrderVarientDto())
                .ToList()
        };
    }
    
    public static DeliveryOrderItemDto toDeliveryOrderItemDto(this OrderItem item)
    {
        return new DeliveryOrderItemDto 
        {
            Id = item.Id,
            OrderId = item.OrderId,
            OrderItemStatus = item.Status.ToString(),
            Price = item.Price,
            Product = item.ToOrderProductDto(),
            Quanity = item?.Quanity ?? 0,
            ProductVarient = item
                ?.OrderProductsVarients
                ?.Select(ost => ost.toOrderVarientDto())
                .ToList(),
            Address = null,
        };
    }
    
}