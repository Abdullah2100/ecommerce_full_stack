using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.domain.Interface;

public interface IBannerRepository:IRepository<Banner>
{
    Task<Banner?> getBanner(Guid id);
    Task<Banner?> getBanner(Guid id,Guid storeId);
    
    Task<List<Banner>> getBanners(Guid id,int pageNumber ,int pageSize);
    Task<List<Banner>> getBanners(int pageNumber ,int pageSize);
    Task<List<Banner>> getBanners(int randomLenght);


    void deleteAsync(Guid id);

}