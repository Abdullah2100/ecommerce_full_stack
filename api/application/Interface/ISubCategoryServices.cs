using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface ISubCategoryServices
{
    Task<Result<SubCategoryDto?>> CreateSubCategory(Guid userId,CreateSubCategoryDto subCategoryDto);
    Task<Result<SubCategoryDto?>> UpdateSubCategory(Guid userId,UpdateSubCategoryDto subCategoryDto);
    
    Task<Result<bool>> DeleteSubCategory(Guid id,Guid userId);
    
    Task<Result<List<SubCategoryDto>>> GetSubCategories(Guid id, int page, int length);
    Task<Result<List<SubCategoryDto>>> GetSubCategoryAll(Guid adminId, int page, int length);
}