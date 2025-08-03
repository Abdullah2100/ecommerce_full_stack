using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IBannerRepository:IRepository<Banner>
{
    Task<Banner?> getBanner(Guid id);
    Task<Banner?> getBanner(Guid id,Guid storeId);
    
    Task<List<Banner>> getBanners(Guid id,int pageNumber ,int pageSize);
    Task<List<Banner>> getBanners(int pageNumber ,int pageSize);
    Task<List<Banner>> getBanners(int randomLenght);

}