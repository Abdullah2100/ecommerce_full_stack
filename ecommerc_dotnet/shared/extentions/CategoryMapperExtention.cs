using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class CategoryMapperExtention
{
    public static CategoryDto toDto(this Category category, string url)
    {
        return new CategoryDto
        {
            Id = category.Id,
            Image = string.IsNullOrEmpty(category.Image) ? "" : url + category.Image,
            Name = category.Name
        };
    }

    public static bool isEmpty(this UpdateCategoryDto category)
    {
        return string.IsNullOrWhiteSpace(category.Name) &&
               category.Image == null;
    }
}