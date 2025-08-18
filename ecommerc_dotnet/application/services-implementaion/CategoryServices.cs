using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class CategoryServices(
    IWebHostEnvironment host,
    IConfig config,
    ICategoryRepository categoryRepository,
    IUserRepository userRepository)
    : ICategoryServices
{
    public async Task<Result<CategoryDto?>> createCategory(CreateCategoryDto categoryDto, Guid adminId)
    {
        User? user = await userRepository
            .getUser(adminId);

        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
                return new Result<CategoryDto?>
                (
                    data: null,
                    message: isValid.Message,
                    isSeccessful: false,
                    statusCode: isValid.StatusCode 
                );
        }

        if (await categoryRepository.isExist(categoryDto.Name))
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there are category with the same name",
                isSeccessful: false,
                statusCode: 400
            );
        }

        string? imagePath = await clsUtil
            .saveFile(categoryDto.Image,
                EnImageType.CATEGORY,
                host);

        if (imagePath is null)
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there error while saving image to server",
                isSeccessful: false,
                statusCode: 400
            );
        Guid categoryId = clsUtil.generateGuid();

        Category category = new Category
        {
            Id = categoryId,
            Name = categoryDto.Name,
            Image = imagePath,
            IsBlocked = false,
            OwnerId = user!.Id
        };
        int result = await categoryRepository.addAsync(category);

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while adding category",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<CategoryDto?>> updateCategory(UpdateCategoryDto categoryDto, Guid adminId)
    {
        if (categoryDto.isEmpty())
            return new Result<CategoryDto?>
            (
                data: null,
                message: "no change found",
                isSeccessful: false,
                statusCode: 200
            );

        User? user = await userRepository
            .getUser(adminId);
        
        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: isValid.Message,
                isSeccessful: false,
                statusCode: isValid.StatusCode 
            );
        } 
        if (categoryDto.Name is not null)
            if (await categoryRepository.isExist(categoryDto.Name,categoryDto.Id))
            {
                return new Result<CategoryDto?>
                (
                    data: null,
                    message: "there are category with the same name",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        Category? category = await categoryRepository.getCategory(categoryDto.Id);


        if (category is null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "category not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        string? image = null;

        if (categoryDto?.Image is not null)
        {
            if (categoryDto?.Image is not null)
                clsUtil.deleteFile(category.Image, host);
            image = await clsUtil
                .saveFile(categoryDto!.Image!,
                    EnImageType.CATEGORY,
                    host);
        }

        category.Name = categoryDto?.Name ?? category.Name;
        category.Image = image ?? category.Image;
        category.UpdatedAt = DateTime.Now;
        int result = await categoryRepository.updateAsync(category);

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while update category",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteCategory(Guid categoryId, Guid adminId)
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
        if (!(await categoryRepository.isExist(categoryId)))
        {
            return new Result<bool>
            (
                data: false,
                message: "category not found",
                isSeccessful: false,
                statusCode: 400
            );
        }

        int result = await categoryRepository.deleteAsync(categoryId);
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete category",
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

    public async Task<Result<List<CategoryDto>>> getCategories(int pageNumber, int pageSize)
    {
        List<CategoryDto> categories = (await categoryRepository.getAllAsync(pageNumber, pageSize))
            .Select(ca => ca.toDto(config.getKey("url_file")))
            .ToList();
        return new Result<List<CategoryDto>>
        (
            data: categories,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }
}