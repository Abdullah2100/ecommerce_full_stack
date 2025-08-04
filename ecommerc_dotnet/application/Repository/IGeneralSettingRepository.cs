using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IGeneralSettingRepository:IRepository<GeneralSetting>
{

    Task<GeneralSetting?> getGeneralSetting(Guid id);
    
    Task<bool> isExist(Guid id);
    Task<bool> isExist(string name);
    Task<bool> isExist(Guid id,string name);
}