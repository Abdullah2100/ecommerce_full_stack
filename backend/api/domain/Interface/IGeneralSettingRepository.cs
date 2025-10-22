using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.domain.Interface;

public interface IGeneralSettingRepository:IRepository<GeneralSetting>
{

    Task<GeneralSetting?> getGeneralSetting(Guid id);
    Task<IEnumerable<GeneralSetting>> getgenralsettings(int page, int length);
    
    Task<bool> isExist(Guid id);
    Task<bool> isExist(string name);
    Task<bool> isExist(Guid id,string name);

    void delete(Guid id);
}