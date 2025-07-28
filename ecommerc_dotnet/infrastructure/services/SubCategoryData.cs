using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class SubCategoryData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;

    public SubCategoryData(
        AppDbContext dbContext,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _unitOfWork = unitOfWork;
    }

    private async Task<SubCategoryDto?> getSubCategory(Guid id)
    {
        SubCategory? subCategory = await _dbContext
            .SubCategories
            .FindAsync(id);

        if (subCategory == null) return null;

        return subCategory.toDto();
    }


    public async Task<List<SubCategoryDto>> getSubCategory(
        Guid id,
        int pageNumber,
        int pageSize = 25)
    {
        IEnumerable<SubCategory> subCategories =
            await _unitOfWork
                .SubCategoryRepository
                .getAllAsync(pageNumber, pageSize);

        return subCategories.Select(subCategory => subCategory.toDto()).ToList();
    }


    public async Task<bool> isExist(Guid storeId, Guid subcategoryId)
    {
        return await _dbContext
                .SubCategories
                .AsNoTracking()
                .AnyAsync(sub => sub.Id == subcategoryId && sub.StoreId == storeId)
            ;
    }

    public async Task<bool> isExist(Guid storeId, string name)
    {
        return await _dbContext
            .SubCategories
            .AsNoTracking()
            .AnyAsync(sub => sub.Name == name && sub.StoreId == storeId);
    }

    public async Task<int?> countByStoreId(Guid? storeId)
    {
        if (storeId is null) return null;
        try
        {
            return await _dbContext
                .SubCategories
                .Where(sub => sub.StoreId == storeId)
                .AsNoTracking()
                .CountAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }


    public async Task<SubCategoryDto?> createSubCategory(
        Guid cateogyId,
        Guid storeId,
        string name)
    {
        Guid id = clsUtil.generateGuid();

        SubCategory subCategory = new SubCategory
        {
            Id = id,
            CategoryId = cateogyId,
            StoreId = storeId,
            Name = name,
            UpdatedAt = null,
            CreatedAt = DateTime.Now,
        };
        await _unitOfWork
            .SubCategoryRepository
            .addAsync(subCategory);

        int result = await _unitOfWork.Complate();
        if (result == 0) return null;

        return subCategory?.toDto();
    }

    public async Task<SubCategoryDto?> updateSubCategory(
        Guid id,
        string? name,
        Guid? categoryId)
    {
        SubCategory? subCategory = await _dbContext
            .SubCategories
            .FindAsync(id);

        if (subCategory == null) return null;

        subCategory.Name = name ?? subCategory.Name;
        subCategory.CategoryId = categoryId ?? subCategory.CategoryId;
        subCategory.UpdatedAt = DateTime.Now;

        int result = await _unitOfWork.Complate();

        if (result == 0) return null;

        return subCategory.toDto();
    }

    public async Task<bool?> deleteSubCategory(Guid id)
    {
        await _dbContext
            .SubCategories.Where(sc => sc.Id == id)
            .ExecuteDeleteAsync();

        int result = await _dbContext.SaveChangesAsync();
        return (result != 0);
    }
}