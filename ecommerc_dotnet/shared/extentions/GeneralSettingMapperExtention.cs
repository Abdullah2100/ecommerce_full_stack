using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.Presentation.dto.Request;

namespace ecommerc_dotnet.shared.extentions;

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
    
    public static bool isEmpty(this UpdateGeneralSettingDto generalSetting)
    {
        return string.IsNullOrEmpty(generalSetting.Name?.Trim()) &&
               generalSetting.Value != null;
    }
}