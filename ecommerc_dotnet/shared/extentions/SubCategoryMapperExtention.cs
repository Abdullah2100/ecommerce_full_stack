using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

/// <summary>
/// Provides extension methods for mapping between SubCategory entity and DTOs
/// </summary>
public static class SubCategoryMapperExtensions
{
    /// <summary>
    /// Maps a SubCategory entity to a SubCategoryDto
    /// </summary>
    /// <param name="subCategory">The SubCategory entity to map</param>
    /// <returns>A new instance of SubCategoryDto</returns>
    public static SubCategoryDto ToDto(this SubCategory subCategory)
    {
        if (subCategory == null)
            throw new ArgumentNullException(nameof(subCategory));

        return new SubCategoryDto 
        {
            Id = subCategory.Id,
            Name = subCategory.Name,
            CategoryId = subCategory.CategoryId,
            StoreId = subCategory.StoreId
        };
    }

    /// <summary>
    /// Determines if the UpdateSubCategoryDto has no update values
    /// </summary>
    /// <param name="dto">The DTO to check</param>
    /// <returns>True if all properties are null or whitespace, otherwise false</returns>
    public static bool IsEmpty(this UpdateSubCategoryDto dto)
    {
        if (dto == null)
            throw new ArgumentNullException(nameof(dto));

        return string.IsNullOrWhiteSpace(dto.Name) &&
               dto.CategoryId == null;
    }
}