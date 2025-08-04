using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.application.Repository;

public interface ICategoryRepository:IRepository<Category>
{

    Task<Category?> getCategory(Guid id);
    Task<bool> isExist(Guid id);
    Task<bool> isExist(string name);
    Task<bool> isExist(string name,Guid id);
}