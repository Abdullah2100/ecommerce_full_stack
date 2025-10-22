using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.services;

public interface ISubCategoryServices
{
    Task<Result<SubCategoryDto?>> createSubCategory(Guid userId,CreateSubCategoryDto subCategoryDto);
    Task<Result<SubCategoryDto?>> updateSubCategory(Guid userId,UpdateSubCategoryDto subCategoryDto);
    
    Task<Result<bool>> deleteSubCategory(Guid id,Guid userId);
    
    Task<Result<List<SubCategoryDto>>> getSubCategories(Guid id, int page, int length);
    Task<Result<List<SubCategoryDto>>> getSubCategoryAll(Guid adminId, int page, int length);
}