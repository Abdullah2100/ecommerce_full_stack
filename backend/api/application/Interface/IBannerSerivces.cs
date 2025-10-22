using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface IBannerSerivces
{
    Task<Result<BannerDto?>> createBanner(Guid userId, CreateBannerDto bannerDto);
    Task<Result<bool>> deleteBanner(Guid id, Guid userId);

    Task<Result<List<BannerDto>>> getBannersAll(Guid adminId, int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> getBanners(Guid storeId, int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> getBanners(int randomLenght);
}