using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class GeneralSettingServices:IGeneralSettingServices
{
    private readonly IGeneralSettingRepository _generalSettingRepository;
    private readonly IUserRepository _userRepository;

    public GeneralSettingServices(
        IGeneralSettingRepository generalSettingRepository,
        IUserRepository userRepository
    )
    {
        _generalSettingRepository = generalSettingRepository;
        _userRepository = userRepository;
    }
    public async Task<Result<GeneralSettingDto?>> createGeneralSetting(
        Guid adminId, 
        GeneralSettingDto settingDto)
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await _generalSettingRepository.isExist(settingDto.Name))
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "there are  generalsetting with the same name",
                isSeccessful: false,
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
        int result = await _generalSettingRepository.addAsync(generalSetting);
        

        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while adding generalsetting",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.toDto(),
            message: "",
            isSeccessful: true,
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
                isSeccessful: false,
                statusCode: 200
            ); 
        
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        GeneralSetting? generalSetting = await _generalSettingRepository.getGeneralSetting(id);
        if (generalSetting is null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "no generalsetting found",
                isSeccessful: false,
                statusCode: 404
            );
        }
        generalSetting.Name = settingDto.Name??generalSetting.Name;
        generalSetting.Value = settingDto.Value??generalSetting.Value;
        generalSetting.UpdatedAt = DateTime.Now;
       
        int result = await _generalSettingRepository.addAsync(generalSetting);
        

        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while update generalsetting",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
        
        
    }

    public async Task<Result<bool>> deleteGeneralSetting(Guid id, Guid adminId)
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<bool>
            (
                data: false, 
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }
        
        if (!(await _generalSettingRepository.isExist(id)))
        {
            return new Result<bool>
            (
                data: false, 
                message: "generalSetting not found",
                isSeccessful: false,
                statusCode: 400
            );
        }

        int result = await _generalSettingRepository.deleteAsync(id);
        

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,  
                message: "error while delete generalsetting",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: false, 
            message: "",
            isSeccessful: true,
            statusCode: 204
        ); 
    }

    public async Task<Result<List<GeneralSettingDto>>> getGeneralSettings(int pageNum, int pageSize)
    {
        List<GeneralSettingDto>  categories = (await _generalSettingRepository.getAllAsync(pageNum, pageSize))
            .Select(ca => ca.toDto())
            .ToList();
        return new Result<List<GeneralSettingDto>>
        (
            data: categories,
            message: "",
            isSeccessful: true,
            statusCode: 200
        ); 
    }
}