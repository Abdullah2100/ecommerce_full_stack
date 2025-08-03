using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.infrastructure.repositories;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class VarientServices : IVarientServices
{
    private readonly IUserRepository _userRepository;
    private readonly IVarientRepository _varientRepository;

    public VarientServices(
        IUserRepository userRepository,
        IVarientRepository varientRepository
    )
    {
        _userRepository = userRepository;
        _varientRepository = varientRepository;
    }

    public async Task<Result<VarientDto?>> createVarient(
        CreateVarientDto varientDto,
        Guid adminId
    )
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await _varientRepository.isExist(varientDto.Name))
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

        int result = await _varientRepository
            .addAsync(
                new Varient
                {
                    Id = id,
                    Name = varientDto.Name
                }
            );

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

        Varient? varient = await _varientRepository.getVarient(id);
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
                statusCode: 201
            );

        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<VarientDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (varientDto.Name is not null)
            if (await _varientRepository.isExist(varientDto.Name))
            {
                return new Result<VarientDto?>
                (
                    data: null,
                    message: "there are varient with the same name",
                    isSeccessful: false,
                    statusCode: 400
                );
            }

        Varient? varient = await _varientRepository.getVarient(varientDto.Id);

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

        int result = await _varientRepository
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

        

        Varient? varient = await _varientRepository.getVarient(vairentId);

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


        int result = await _varientRepository
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
         List<VarientDto> varients =(await _varientRepository
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