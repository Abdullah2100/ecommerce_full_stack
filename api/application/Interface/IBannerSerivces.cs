using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IBannerSerivces
{
    Task<Result<BannerDto?>> CreateBanner(Guid userId, CreateBannerDto bannerDto);
    Task<Result<bool>> DeleteBanner(Guid id, Guid userId);

    Task<Result<List<BannerDto>>> GetBannersAll(Guid adminId, int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> GetBanners(Guid storeId, int pageNumber, int pageSize);
    Task<Result<List<BannerDto>>> GetBanners(int randomLenght);
}