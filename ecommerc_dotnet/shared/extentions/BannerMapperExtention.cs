using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class BannerMapperExtention
{
    public static BannerDto toDto(this Banner banner,string url)
    {
        return new BannerDto
        {
            CreatedAt = banner.CreatedAt,
            EndAt = banner.EndAt,
            Id = banner.Id,
            Image = string.IsNullOrEmpty(banner.Image) ? "" :url+ banner.Image,
            StoreId = banner.StoreId,
        };
    }

    
    
}