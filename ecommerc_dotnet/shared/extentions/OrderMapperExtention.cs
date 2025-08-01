using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class OrderMapperExtention
{
    public static OrderDto toDto(this Order order)
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
                .Select(it=>it.toOrderItemDto())
                .ToList()
        };
    }
    
    public static DeliveryOrderDto toDeliveryDto(this Order order)
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
                .Select(it=>it.toDeliveryOrderItemDto())
                .ToList()
        };
    }
}