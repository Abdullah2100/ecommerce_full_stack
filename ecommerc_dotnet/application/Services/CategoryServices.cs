using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services;

public class CategoryServices(
    IWebHostEnvironment host,
    IConfig config,
    IUnitOfWork unitOfWork,
    IFileServices fileServicee)
    : ICategoryServices
{
    public async Task<Result<CategoryDto?>> createCategory(CreateCategoryDto categoryDto, Guid adminId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (await unitOfWork.CategoryRepository.isExist(categoryDto.Name))
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there are category with the same name",
                isSuccessful: false,
                statusCode: 400
            );
        }

        string? imagePath = await fileServicee
            .saveFile(categoryDto.Image,
                EnImageType.CATEGORY);

        if (imagePath is null)
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there error while saving image to server",
                isSuccessful: false,
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
        unitOfWork.CategoryRepository.add(category);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while adding category",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category?.toDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
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
                isSuccessful: false,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var isValid = user.isValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (categoryDto.Name is not null)
            if (await unitOfWork.CategoryRepository.isExist(categoryDto.Name, categoryDto.Id))
            {
                return new Result<CategoryDto?>
                (
                    data: null,
                    message: "there are category with the same name",
                    isSuccessful: false,
                    statusCode: 400
                );
            }

        Category? category = await unitOfWork.CategoryRepository.getCategory(categoryDto.Id);


        if (category is null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "category not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        string? image = null;

        if (categoryDto?.Image is not null)
        {
            if (categoryDto?.Image is not null)
                fileServicee.deleteFile(category.Image);
            image = await fileServicee 
                .saveFile(categoryDto!.Image!,
                    EnImageType.CATEGORY);
        }

        category.Name = categoryDto?.Name ?? category.Name;
        category.Image = image ?? category.Image;
        category.UpdatedAt = DateTime.Now;

        unitOfWork.CategoryRepository.update(category);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while update category",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category.toDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteCategory(Guid categoryId, Guid adminId)
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
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (!(await unitOfWork.CategoryRepository.isExist(categoryId)))
        {
            return new Result<bool>
            (
                data: false,
                message: "category not found",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.CategoryRepository.delete(categoryId);
        int result = await unitOfWork.saveChanges();
        
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete category",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<CategoryDto>>> getCategories(int pageNumber, int pageSize)
    {
        List<CategoryDto> categories = (await unitOfWork.CategoryRepository.getCategories(pageNumber, pageSize))
            .Select(ca => ca.toDto(config.getKey("url_file")))
            .ToList();
        return new Result<List<CategoryDto>>
        (
            data: categories,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}