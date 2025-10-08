using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.infrastructure.repositories;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public class VarientServices(IUnitOfWork unitOfWork)
    : IVarientServices
{
    public async Task<Result<VarientDto?>> createVarient(
        CreateVarientDto varientDto,
        Guid adminId
    )
    {
        User? user = await unitOfWork.UserRepository
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

        if (await unitOfWork.VarientRepository.isExist(varientDto.Name))
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

        unitOfWork.VarientRepository.add(varient);
        int result = await unitOfWork.saveChanges();

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

        User? user = await unitOfWork.UserRepository
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
            if (await unitOfWork.VarientRepository.isExist(varientDto.Name, varientDto.Id))
            {
                return new Result<VarientDto?>
                (
                    data: null,
                    message: "there are varient with the same name",
                    isSeccessful: false,
                    statusCode: 400
                );
            }

        Varient? varient = await unitOfWork.VarientRepository.getVarient(varientDto.Id);

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

        unitOfWork.VarientRepository.update(varient);
        int result = await unitOfWork.saveChanges();

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
        User? user = await unitOfWork.UserRepository
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


        Varient? varient = await unitOfWork.VarientRepository.getVarient(vairentId);

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


        unitOfWork.VarientRepository
            .deleteAsync(vairentId);
        int result = await unitOfWork.saveChanges();

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

    public async Task<Result<List<VarientDto>>> getVarients(int page, int pageSize)
    {
        List<VarientDto> varients = (await unitOfWork.VarientRepository
                .getVarients(page, pageSize))
            .Select(va => va.toDto())
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