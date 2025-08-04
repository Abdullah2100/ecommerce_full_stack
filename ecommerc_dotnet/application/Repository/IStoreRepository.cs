using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IStoreRepository:IRepository<Store>
{
    Task<Store?> getStore(Guid id);
    Task<Store?> getStoreByUserId(Guid id);
    
    Task<int> getStoresCount();
    
    Task<bool> isExist(string name);
    Task<bool> isExist(Guid id);
    Task<bool> isExist(Guid id,Guid subCategoryId);
}