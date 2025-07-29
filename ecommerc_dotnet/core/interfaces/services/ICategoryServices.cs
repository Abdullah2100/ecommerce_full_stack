using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.services;

public interface ICategoryServices
{
    Task<Result<CategoryDto?>> createCategory(CreateCategoryDto categoryDto, Guid adminId);
    Task<Result<CategoryDto?>> updateCategory(UpdateCategoryDto categoryDto, Guid adminId);
    Task<Result<bool>> deleteCategory(Guid categoryId, Guid adminId);
    
    
    Task<Result<List<CategoryDto>>> getCategories(int pageNumber, int pageSize);
}