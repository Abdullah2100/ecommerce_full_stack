using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.shared.extentions;


public static class SubCategoryMapperExtensions
{
    public static SubCategoryDto toDto(this SubCategory subCategory)
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

    public static bool isEmpty(this UpdateSubCategoryDto dto)
    {
        if (dto == null)
            throw new ArgumentNullException(nameof(dto));

        return string.IsNullOrWhiteSpace(dto.Name) &&
               dto.CategoryId == null;
    }
}