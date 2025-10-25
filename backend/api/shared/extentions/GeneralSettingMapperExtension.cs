using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.extentions;

public static class GeneralSettingMapperExtension
{
    public static GeneralSettingDto ToDto(this GeneralSetting generalSetting)
    {
        return new GeneralSettingDto
        {
            Name = generalSetting.Name,
            Value = generalSetting.Value,
        };
    }
    
    public static bool IsEmpty(this UpdateGeneralSettingDto generalSetting)
    {
        return string.IsNullOrEmpty(generalSetting.Name?.Trim()) &&
               generalSetting.Value != null;
    }
}