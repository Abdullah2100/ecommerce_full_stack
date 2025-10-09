using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.Presentation.dto.Response;

namespace ecommerc_dotnet.shared.extentions;

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
            Address = item.Store?.Addresses==null ||item.Store.Addresses.Count<1? new List<AddressWithTitleDto>():
                item.Store?.Addresses.Select(ad=>
                new AddressWithTitleDto() 
                {
                    Longitude = ad?.Longitude,
                    Latitude = ad?.Latitude,
                    Title = ad?.Title??"",
                }
                ).ToList(),
            ProductVarient =item.OrderProductsVarients==null||item.OrderProductsVarients.Count<1?new List<OrderVarientDto>():
                
                item
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