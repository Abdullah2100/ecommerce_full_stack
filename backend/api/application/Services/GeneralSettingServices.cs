using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto.Request;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public class GeneralSettingServices(
    IUnitOfWork unitOfWork
)
    : IGeneralSettingServices
{
    public async Task<Result<GeneralSettingDto?>> createGeneralSetting(
        Guid adminId,
        GeneralSettingDto settingDto)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var validation = user.isValidateFunc();
        if (validation is not null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (await unitOfWork.GeneralSettingRepository.isExist(settingDto.Name))
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "there are  generalsetting with the same name",
                isSuccessful: false,
                statusCode: 400
            );
        }

        GeneralSetting generalSetting = new GeneralSetting
        {
            CreatedAt = DateTime.Now,
            Id = clsUtil.generateGuid(),
            Name = settingDto.Name,
            Value = settingDto.Value
        };
        unitOfWork.GeneralSettingRepository.add(generalSetting);
        int result = await unitOfWork.saveChanges();


        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while adding generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.toDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<GeneralSettingDto?>> updateGeneralSetting(
        Guid id, Guid adminId,
        UpdateGeneralSettingDto settingDto
    )
    {
        if (settingDto.isEmpty())
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "no change found",
                isSuccessful: false,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var validation = user.isValidateFunc();
        if (validation is not null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        GeneralSetting? generalSetting = await unitOfWork.GeneralSettingRepository.getGeneralSetting(id);
        if (generalSetting is null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "no generalsetting found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        generalSetting.Name = settingDto.Name ?? generalSetting.Name;
        generalSetting.Value = settingDto.Value ?? generalSetting.Value;
        generalSetting.UpdatedAt = DateTime.Now;

        unitOfWork.GeneralSettingRepository.add(generalSetting);
        int result = await unitOfWork.saveChanges();


        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while update generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.toDto(),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteGeneralSetting(Guid id, Guid adminId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);
        var validation = user.isValidateFunc();
        if (validation is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (!(await unitOfWork.GeneralSettingRepository.isExist(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "generalSetting not found",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.GeneralSettingRepository.delete(id);
        int result = await unitOfWork.saveChanges();


        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: false,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<GeneralSettingDto>>> getGeneralSettings(int pageNum, int pageSize)
    {
        List<GeneralSettingDto> categories = (await unitOfWork.GeneralSettingRepository.getgenralsettings(pageNum, pageSize))
            .Select(ca => ca.toDto())
            .ToList();
        return new Result<List<GeneralSettingDto>>
        (
            data: categories,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}