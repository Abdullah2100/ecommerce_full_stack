using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto.Request;

namespace ecommerc_dotnet.application.Interface;

public interface IGeneralSettingServices
{
   Task<Result<GeneralSettingDto?>> createGeneralSetting(Guid adminId, GeneralSettingDto settingDto);
   Task<Result<GeneralSettingDto?>> updateGeneralSetting(Guid id ,Guid adminId,UpdateGeneralSettingDto settingDto);
   
   Task<Result<bool>> deleteGeneralSetting(Guid id,Guid adminId);
   
   Task<Result<List<GeneralSettingDto>>> getGeneralSettings(int pageNum, int pageSize);
}