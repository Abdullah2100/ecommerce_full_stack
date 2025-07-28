using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class StoreMapperExtention
{
    public static StoreDto toDto(this Store store, string url)
    {
        return new StoreDto
        {
            Id = store.Id,
            IsBlocked = store.IsBlock,
            Longitude = store.Addresses?.FirstOrDefault()?.Longitude,
            Latitude = store.Addresses?.FirstOrDefault()?.Latitude,
            Name = store.Name,
            SmallImage = string.IsNullOrEmpty(store.SmallImage) ? "" : url + store.SmallImage,
            WallpaperImage = string.IsNullOrEmpty(store.WallpaperImage) ? "" : url + store.WallpaperImage,
            UpdatedAtAt = store.UpdatedAt,
            UserId = store.UserId,
            UserName = store.user.Name,
        };
    }
    
    public static bool isEmpty(this UpdateStoreDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name) &&
               dto.WallpaperImage == null &&
               dto.SmallImage == null &&
               dto.Longitude == null &&
               dto.Latitude == null;
    }
}