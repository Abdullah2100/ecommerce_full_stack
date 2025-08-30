using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class OrderMapperExtention
{
    public static OrderDto toDto(this Order order,string url)
    {
        return new OrderDto
        {
            Id = order.Id,
            DeliveryFee = order.DistanceFee,
            Name = order.User.Name,
            Longitude = order.Longitude,
            Latitude = order.Latitude,
            Status = order.Status,
            TotalPrice = order.TotalPrice,
            UserPhone = order.User.Phone,
            OrderItems = order
                .Items
                .Select(it=>it.toOrderItemDto(url))
                .ToList()
        };
    }
    
    public static DeliveryOrderDto toDeliveryDto(this Order order,string url)
    {
        return new DeliveryOrderDto 
        {
            Id = order.Id,
            DeliveryFee = order.DistanceFee,
            Name = order.User.Name,
            Longitude = order.Longitude,
            Latitude = order.Latitude,
            Status = order.Status,
            TotalPrice = order.TotalPrice,
            UserPhone = order.User.Phone,
            OrderItems = order
                .Items
                .Select(it=>it.toDeliveryOrderItemDto(url))
                .ToList()
        };
    }
}