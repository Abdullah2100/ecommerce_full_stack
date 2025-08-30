using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class SubCategoryServices(
    IUserRepository userRepository,
    IStoreRepository storeRepository,
    ISubCategoryRepository subCategoryRepository,
    ICategoryRepository categoryRepository)
    : ISubCategoryServices
{
    private readonly IStoreRepository _storeRepository = storeRepository;

    public async Task<Result<SubCategoryDto?>> createSubCategory(
        Guid userId,
        CreateSubCategoryDto subCategoryDto
    )
    {
        User? user = await userRepository
            .getUser(userId);

        var isValide = user.isValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<SubCategoryDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        int count = await subCategoryRepository.getSubCategoriesCount(user.Store.Id);

        if (count == 20)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "store can maximum 20 subcategories",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Guid id = clsUtil.generateGuid();

        SubCategory subCategory = new SubCategory
        {
            Id = id,
            CategoryId = subCategoryDto.CategoryId,
            StoreId = user.Store.Id,
            Name = subCategoryDto.Name,
            UpdatedAt = null,
            CreatedAt = DateTime.Now,
        };
        int result = await subCategoryRepository.addAsync(subCategory);

        if (result == 0)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "error while adding new subcategory",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<SubCategoryDto?>
        (
            data: subCategory.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<SubCategoryDto?>> updateSubCategory(
        Guid userId,
        UpdateSubCategoryDto subCategoryDto
    )
    {
        if (subCategoryDto.isEmpty())
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "",
                isSeccessful: true,
                statusCode: 200
            );


        User? user = await userRepository
            .getUser(userId);

        var isValide = user.isValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<SubCategoryDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        SubCategory? subCategory = await subCategoryRepository
            .getSubCategory(subCategoryDto.Id);

        if (subCategory is null || subCategory.StoreId != user!.Store!.Id)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "subcategory not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (subCategoryDto.CategoryId is not null &&
            !(await categoryRepository.isExist((Guid)subCategoryDto!.CategoryId))
           )
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "invalide category",
                isSeccessful: false,
                statusCode: 404
            );
        }

        subCategory.Name = subCategoryDto.Name ?? subCategory.Name;
        subCategory.CategoryId = subCategoryDto.CategoryId ?? subCategory.CategoryId;
        subCategory.UpdatedAt = DateTime.Now;
        int result = await subCategoryRepository.updateAsync(subCategory);
        if (result == 0)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "error while update subcategory",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<SubCategoryDto?>
        (
            data: subCategory.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteSubCategory(Guid id, Guid userId)
    {
        User? user = await userRepository
            .getUser(userId);

        var isValide = user.isValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSeccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        SubCategory? subCategory = await subCategoryRepository
            .getSubCategory(id);

        if (subCategory is null || subCategory.StoreId != user!.Store!.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "subcategory not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int result = await subCategoryRepository.deleteAsync(id);
        if (result == 0)
            return new Result<bool>
            (
                data: false,
                message: "error while deleting subcategory",
                isSeccessful: false,
                statusCode: 404
            );

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<SubCategoryDto>>> getSubCategories(Guid id, int page, int length)
    {
        List<SubCategoryDto> subCategories = (await subCategoryRepository
                .getSubCategories(id, page, length))
            .Select(su => su.toDto())
            .ToList();
        return (subCategories.Count > 0) switch
        {
            true =>
                new Result<List<SubCategoryDto>>
                (
                    data: subCategories,
                    message: "",
                    isSeccessful: true,
                    statusCode: 200
                ),
            _ => new Result<List<SubCategoryDto>>
            (
                data: new List<SubCategoryDto>(),
                message: "",
                isSeccessful: true,
                statusCode: 204
            )
        };

    }

    public async Task<Result<List<SubCategoryDto>>> getSubCategoryAll(
        Guid adminId,
        int page,
        int length)
    {
        User? user = await userRepository
            .getUser(adminId);
        var isValide = user.isValidateFunc(isAdmin: true);

        if (isValide is not null)
        {
            return new Result<List<SubCategoryDto>>(
                isSeccessful: false,
                data: new List<SubCategoryDto>(),
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        List<SubCategoryDto> subcategories = (await subCategoryRepository
                .getAllAsync(page: page, length: length))
            .Select(ba => ba.toDto())
            .ToList();

        return new Result<List<SubCategoryDto>>
        (
            data: subcategories,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }
}