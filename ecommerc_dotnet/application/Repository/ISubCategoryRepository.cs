using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface ISubCategoryRepository:IRepository<SubCategory>
{
    Task<SubCategory?> getSubCategory(Guid id);
    Task<List<SubCategory>> getSubCategories(Guid storeId,int pageNumber,int pageSize);
    Task<int> getSubCategoriesCount(Guid storeId);

    Task<bool> isExist(Guid id);
    Task<bool> isExist(Guid storeId,string name);
    Task<bool> isExist(Guid storeId,Guid id);

}