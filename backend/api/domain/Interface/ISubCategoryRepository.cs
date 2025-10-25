using api.domain.entity;

namespace api.domain.Interface;

public interface ISubCategoryRepository:IRepository<SubCategory>
{
    Task<SubCategory?> GetSubCategory(Guid id);
    Task<List<SubCategory>> GetSubCategories(Guid storeId,int pageNumber,int pageSize);
    Task<List<SubCategory>> GetSubCategories(int pageNumber,int pageSize);
    Task<int> GetSubCategoriesCount(Guid storeId);

    Task<bool> IsExist(Guid id);
    Task<bool> IsExist(Guid storeId,string name);
    Task<bool> IsExist(Guid storeId,Guid id);
    void Delete(Guid id);
}