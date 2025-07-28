using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class VarientMapperExtention
{
    public static VarientDto toDto(this Varient varient)
    {
        return new VarientDto
        {
            Id = varient.Id,
            Name = varient.Name,

        };
    }

    public static bool isEmpty(this UpdateVarientDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name?.Trim());

    }
}
    
