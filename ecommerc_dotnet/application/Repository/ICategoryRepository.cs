using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface ICategoryRepository:IRepository<Category>
{

    Task<Category?> getCategory(Guid id);
    Task<bool> isExist(Guid id);
    Task<bool> isExist(string name);
}