using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;

namespace ecommerc_dotnet.mapper;

public static class OrderItemMapperExtention
{
    public static OrderItemDto toOrderItemDto(this OrderItem item,string url)
    {
        return new OrderItemDto
        {
            Id = item.Id,
            OrderId = item.OrderId,
            OrderItemStatus = item.Status.ToString(),
            Price = item.Price,
            Product = item.ToOrderProductDto(url),
            Quanity = item?.Quanity ?? 0,
            Address = item.Store.Addresses.Select(ad=>
                new AddressWithTitleDto() 
                {
                    Longitude = ad?.Longitude,
                    Latitude = ad?.Latitude,
                    Title = ad?.Title??"",
                }
                ).ToList(),
            ProductVarient = item
                ?.OrderProductsVarients
                ?.Select(ost => ost.toOrderVarientDto())
                .ToList()
        };
    }
    
    public static DeliveryOrderItemDto toDeliveryOrderItemDto(this OrderItem item,string url)
    {
        return new DeliveryOrderItemDto 
        {
            Id = item.Id,
            OrderId = item.OrderId,
            OrderItemStatus = item.Status.ToString(),
            Price = item.Price,
            Product = item.ToOrderProductDto(url),
            Quanity = item?.Quanity ?? 0,
            ProductVarient = item
                ?.OrderProductsVarients
                ?.Select(ost => ost.toOrderVarientDto())
                .ToList(),
            Address = null,
        };
    }
    
}