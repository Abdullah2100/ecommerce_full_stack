using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.infrastructure.repositories;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class VarientServices(
    IUserRepository userRepository,
    IVarientRepository varientRepository)
    : IVarientServices
{
    public async Task<Result<VarientDto?>> createVarient(
        CreateVarientDto varientDto,
        Guid adminId
    )
    {
        User? user = await userRepository
            .getUser(adminId);
        
        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: isValid.Message,
                isSeccessful: false,
                statusCode: isValid.StatusCode 
            );
        }   
        if (await varientRepository.isExist(varientDto.Name))
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "there are varient with the same name",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Guid id = clsUtil.generateGuid();

        Varient? varient = new Varient
        {
            Id = id,
            Name = varientDto.Name
        };
        
        int result = await varientRepository.addAsync(varient);

        if (result == 0)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "error while adding new varient",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<VarientDto?>
        (
            data: varient?.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<VarientDto?>> updateVarient(
        UpdateVarientDto varientDto,
        Guid adminId
    )
    {
        if (varientDto.isEmpty())
            return new Result<VarientDto?>
            (
                data: null,
                message: "",
                isSeccessful: true,
                statusCode: 200
            );

        User? user = await userRepository
            .getUser(adminId);
        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: isValid.Message,
                isSeccessful: false,
                statusCode: isValid.StatusCode 
            );
        }    

        if (varientDto.Name is not null)
            if (await varientRepository.isExist(varientDto.Name,varientDto.Id))
            {
                return new Result<VarientDto?>
                (
                    data: null,
                    message: "there are varient with the same name",
                    isSeccessful: false,
                    statusCode: 400
                );
            }

        Varient? varient = await varientRepository.getVarient(varientDto.Id);

        if (varient is null)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "varient not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        varient.Name = varientDto.Name ?? varient.Name;

        int result = await varientRepository
            .updateAsync(varient);

        if (result == 0)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "error while update varient",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<VarientDto?>
        (
            data: varient?.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<bool>> deleteVarient(Guid vairentId, Guid adminId)
    {
           

        User? user = await userRepository
            .getUser(adminId);
        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSeccessful: false,
                statusCode: isValid.StatusCode 
            );
        }     

        

        Varient? varient = await varientRepository.getVarient(vairentId);

        if (varient is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "varient not found",
                isSeccessful: false,
                statusCode: 404
            );
        }


        int result = await varientRepository
            .deleteAsync(vairentId);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete varient",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<VarientDto>>> getVarients(int page,int pageSize)
    {
         List<VarientDto> varients =(await varientRepository
             .getAllAsync(page, pageSize))
             .Select(va=>va.toDto())
             .ToList();
         return new Result<List<VarientDto>>
         (
             data: varients,
             message: "",
             isSeccessful: true,
             statusCode: 204
         );
    }

  
}