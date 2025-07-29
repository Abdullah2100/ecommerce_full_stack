using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.services;

public interface ISubCategoryServices
{
    Task<Result<SubCategoryDto?>> createSubCategory(Guid userId,CreateSubCategoryDto subCategoryDto);
    Task<Result<SubCategoryDto?>> updateSubCategory(Guid userId,UpdateSubCategoryDto subCategoryDto);
    
    Task<Result<bool>> deleteSubCategory(Guid id,Guid userId);
    
    Task<Result<List<SubCategoryDto>>> getSubCategory(Guid id, int page, int length);
    Task<Result<List<SubCategoryDto>>> getSubCategoryAll(Guid adminId, int page, int length);
}