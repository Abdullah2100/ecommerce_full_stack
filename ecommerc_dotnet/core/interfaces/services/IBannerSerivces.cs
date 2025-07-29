using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.services;

public interface IBannerSerivces
{
    Task<Result<BannerDto?>> createBanner(Guid userId, CreateBannerDto bannerDto);
    Task<Result<bool>> deleteBanner(Guid id, Guid userId);
    
    Task<Result<List<BannerDto>>> getBannersAll(Guid adminId,int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> getBanners(Guid storeId, int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> getBanners(int randomLenght);
    
}