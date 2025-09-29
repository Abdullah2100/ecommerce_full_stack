using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class AddressMapperExtention
{
    public static AddressDto toDto(this Address address)
    {
        return new AddressDto
        {
            Id = address.Id,
            Latitude = address.Latitude,
            Longitude = address.Longitude,
            Title = address.Title,
            IsCurrent = address.IsCurrent
        };
    }
    
    public static DeliveryAddressDto toDeliveryDto(this Address address)
    {
        return new DeliveryAddressDto 
        {
            Latitude = address.Latitude,
            Longitude = address.Longitude,
        };
    }

    public static bool isEmpty(this UpdateAddressDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Title)
               && dto.Longitude == null
               && dto.Latitude == null;
    }
}