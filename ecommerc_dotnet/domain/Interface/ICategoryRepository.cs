using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;

namespace ecommerc_dotnet.application.Repository;

public interface ICategoryRepository:IRepository<Category>
{

    Task<Category?> getCategory(Guid id);
    
    Task<List<Category>> getCategories(int page, int length);
    Task<bool> isExist(Guid id);
    Task<bool> isExist(string name);
    Task<bool> isExist(string name,Guid id);
    void delete(Guid id);
}