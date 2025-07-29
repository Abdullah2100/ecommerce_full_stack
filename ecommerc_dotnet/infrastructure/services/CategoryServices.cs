using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class CategoryServices : ICategoryServices
{
    private readonly ICategoryRepository _categoryRepository;
    private readonly IUserRepository _userRepository;
    private readonly IWebHostEnvironment _host;
    private readonly IConfig _config;

    public CategoryServices(
        IWebHostEnvironment host,
        IConfig config,
        ICategoryRepository categoryRepository,
        IUserRepository userRepository
    )
    {
        _host = host;
        _config = config;
        _categoryRepository = categoryRepository;
        _userRepository = userRepository;
    }

    public async Task<Result<CategoryDto?>> createCategory(CreateCategoryDto categoryDto, Guid adminId)
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (await _categoryRepository.isExist(categoryDto.Name))
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
                _host);

        if (imagePath is null)
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there error while saving image to server",
                isSeccessful: false,
                statusCode: 400
            );
        Guid categoryId = clsUtil.generateGuid();
        int result = await _categoryRepository.addAsync(new Category
        {
            Id = categoryId,
            Name = categoryDto.Name,
            Image = imagePath,
            IsBlocked = false,
            OwnerId = user.Id
        });

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

        Category? category = await _categoryRepository.getCategory(categoryId);
        return new Result<CategoryDto?>
        (
            data: category?.toDto(_config.getKey("url_file")),
            message: "error while adding category",
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

        User? user = await _userRepository
            .getUser(adminId);
        
        if (user is null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (categoryDto.Name is not null)
            if (await _categoryRepository.isExist(categoryDto.Name))
            {
                return new Result<CategoryDto?>
                (
                    data: null,
                    message: "there are category with the same name",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        Category? category = await _categoryRepository.getCategory(categoryDto.Id);


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
                clsUtil.deleteFile(category.Image, _host);
            image = await clsUtil
                .saveFile(categoryDto!.Image!,
                    EnImageType.CATEGORY,
                    _host);
        }

        category.Name = categoryDto.Name ?? category.Name;
        category.Image = image ?? category.Image;
        category.UpdatedAt = DateTime.Now;
        int result = await _categoryRepository.updateAsync(category);

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
            data: category.toDto(_config.getKey("url_file")),
            message: "error while update category",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> deleteCategory(Guid categoryId, Guid adminId)
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

        if (!(await _categoryRepository.isExist(categoryId)))
        {
            return new Result<bool>
            (
                data: false,
                message: "category not found",
                isSeccessful: false,
                statusCode: 400
            );
        }

        int result = await _categoryRepository.deleteAsync(categoryId);
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
        List<CategoryDto> categories = (await _categoryRepository.getAllAsync(pageNumber, pageSize))
            .Select(ca => ca.toDto(_config.getKey("url_file")))
            .ToList();
        return new Result<List<CategoryDto>>
        (
            data: categories,
            message: "error while update category",
            isSeccessful: true,
            statusCode: 200
        );
    }
}