using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.shared.extentions;

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