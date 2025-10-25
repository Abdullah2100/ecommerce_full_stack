using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface ICategoryServices
{
    Task<Result<CategoryDto?>> CreateCategory(CreateCategoryDto categoryDto, Guid adminId);
    Task<Result<CategoryDto?>> UpdateCategory(UpdateCategoryDto categoryDto, Guid adminId);
    Task<Result<bool>> DeleteCategory(Guid categoryId, Guid adminId);
    Task<Result<List<CategoryDto>>> GetCategories(int pageNumber, int pageSize);
}