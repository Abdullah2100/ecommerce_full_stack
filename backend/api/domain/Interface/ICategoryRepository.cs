using api.domain.entity;

namespace api.domain.Interface;

public interface ICategoryRepository:IRepository<Category>
{

    Task<Category?> GetCategory(Guid id);
    
    Task<List<Category>> GetCategories(int page, int length);
    Task<bool> IsExist(Guid id);
    Task<bool> IsExist(string name);
    Task<bool> IsExist(string name,Guid id);
    void Delete(Guid id);
}