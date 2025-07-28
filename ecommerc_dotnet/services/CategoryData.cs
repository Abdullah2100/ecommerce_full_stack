using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using FirebaseAdmin.Auth;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class CategoryData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;
    private readonly IConfig _config;

    public CategoryData(
        AppDbContext dbContext,
        IUnitOfWork unitOfWork,
        IConfig config
    )
    {
        _dbContext = dbContext;
        _unitOfWork = unitOfWork;
        _config = config;
    }

    public async Task<Category?> getCategory(Guid id )
    {
        return (await _dbContext.Categories.FindAsync(id));
    }
    public async Task<List<CategoryDto>?> getCategories(
        int pageNumber = 1,
        int pageSize = 20)
    {
        return (await _unitOfWork
            .CategoryRepository
            .getAllAsync(pageNumber, pageSize))
            .Select(ca => ca.toDto(_config.getKey("url_file")))
            .ToList();
    }


    public async Task<bool> addNewCategory(
        string name,
        string filePath,
        Guid userId)
    {
        Category categoryObj = new Category
        {
            Id = clsUtil.generateGuid(),
            Name = name,
            Image = filePath,
            IsBlocked = false,
            OwnerId = userId
        };
        await _unitOfWork.CategoryRepository.addAsync(categoryObj);
        int result = await _unitOfWork.Complate();
        return (result != 0);
    }


    public async Task<CategoryDto?> updateCategory(
        Guid id,
        string? name,
        string? filePath
    )
    {
        Category? category = await _dbContext.Categories.FindAsync(id);
        category!.Name = name ?? category.Name;
        category.Image = filePath ?? category.Image;

        int result = await _dbContext.SaveChangesAsync();
        return (result == 0) ? null : category.toDto(_config.getKey("url_file"));
    }


    public async Task<bool> deleteCategory(
        Guid id
    )
    {
        await _unitOfWork.CategoryRepository.deleteAsync(id);
        int result = await _unitOfWork.Complate();
        return (result != 0);
    }

    public async Task<bool> isExist
    (
        string name
    )
    {
        return await _dbContext
            .Categories
            .AsNoTracking()
            .AnyAsync(c => c.Name == name);
    }

    public async Task<bool> isExist
    (
        Guid id
    )
    {
        return await _dbContext.Categories.FindAsync(id) != null;
    }
}