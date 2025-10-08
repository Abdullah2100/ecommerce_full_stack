using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.application.Repository;

public interface IStoreRepository:IRepository<Store>
{
    Task<Store?> getStore(Guid id);
    Task<Store?> getStoreByUserId(Guid id);
    Task<List<Store>> getStores(int page,int length);
    
    Task<int> getStoresCount();
    
    Task<bool> isExist(string name);
    Task<bool> isExist(string name,Guid id);
    Task<bool> isExist(Guid id);
    Task<bool> isExist(Guid id,Guid subCategoryId);

    void delete(Guid id);
}