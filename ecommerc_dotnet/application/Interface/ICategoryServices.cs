using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface ICategoryServices
{
    Task<Result<CategoryDto?>> createCategory(CreateCategoryDto categoryDto, Guid adminId);
    Task<Result<CategoryDto?>> updateCategory(UpdateCategoryDto categoryDto, Guid adminId);
    Task<Result<bool>> deleteCategory(Guid categoryId, Guid adminId);
    
    
    Task<Result<List<CategoryDto>>> getCategories(int pageNumber, int pageSize);
}