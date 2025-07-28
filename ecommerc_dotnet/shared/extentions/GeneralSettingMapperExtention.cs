using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.entity;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class GeneralSettingMapperExtention
{
    public static GeneralSettingDto toDto(this GeneralSetting generalSetting)
    {
        return new GeneralSettingDto
        {
            Name = generalSetting.Name,
            Value = generalSetting.Value,
        };
    }
}