using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;

namespace ecommerc_dotnet.mapper;

public static class DeliveryMapperExtention
{
    public static DeliveryDto toDto(this Delivery delivery, string url)
    {
        return new DeliveryDto
        {
            Id = delivery.Id,
            UserId = delivery.UserId,
            UpdatedAt = delivery.UpdatedAt,
            Address = delivery?.Address?.toDeliveryDto(),
            Analys = null,
            Thumbnail = string.IsNullOrEmpty(delivery?.Thumbnail) ? null : url + delivery.Thumbnail,
            User = delivery?.User.toDeliveryInfoDto(url),
            IsAvailable = delivery?.IsAvaliable ?? false
        };
    }

    public static Result<DeliveryDto>? isValidated(this Delivery? delivery)
    {
        if (delivery is null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "delivery not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (delivery.IsBlocked)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "delivery is blocked",
                isSeccessful: false,
                statusCode: 404
            );
        }

        return null;
    }
}