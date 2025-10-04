using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;

namespace ecommerc_dotnet.mapper;

public static class DeliveryMapperExtention
{
   public static DeliveryDto toDto(this Delivery delivery,string url)
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
}