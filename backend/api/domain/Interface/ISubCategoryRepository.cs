using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.domain.Interface;

public interface ISubCategoryRepository:IRepository<SubCategory>
{
    Task<SubCategory?> getSubCategory(Guid id);
    Task<List<SubCategory>> getSubCategories(Guid storeId,int pageNumber,int pageSize);
    Task<List<SubCategory>> getSubCategories(int pageNumber,int pageSize);
    Task<int> getSubCategoriesCount(Guid storeId);

    Task<bool> isExist(Guid id);
    Task<bool> isExist(Guid storeId,string name);
    Task<bool> isExist(Guid storeId,Guid id);
    void delete(Guid id);
}