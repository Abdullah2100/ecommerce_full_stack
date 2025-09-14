using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.data;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class SubCategoryRepository(AppDbContext context) : ISubCategoryRepository
{
    public async Task<SubCategory?> getSubCategory(Guid id)
    {
       return await context.SubCategories.FindAsync(id);
    }

    public async Task<List<SubCategory>> getSubCategories(
        Guid storeId,
        int  pageNumber,
        int pageSize
        )
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Where(su=>su.StoreId==storeId)
            .Skip((pageNumber - 1) * pageSize)
            .OrderDescending()
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<int> getSubCategoriesCount(Guid storeId)
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Where(su => su.StoreId == storeId)
            .CountAsync();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context.SubCategories.FindAsync(id)!=null;

    }

    public async Task<bool> isExist(Guid storeId, string name)
    {
        return await context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Name == name);
    }

    public async Task<bool> isExist(Guid storeId, Guid id)
    {
        return await context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Id == id);

    }

    public async Task<IEnumerable<SubCategory>> getAllAsync(int page, int length)
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .OrderDescending()
            .Take(length)
            .ToListAsync(); 
    }

    public async Task<int> addAsync(SubCategory entity)
    {
        await context.SubCategories.AddAsync(entity);
        return  await context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(SubCategory entity)
    {
        context.SubCategories.Update(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context.SubCategories.Where(su => su.Id == id)
            .ExecuteDeleteAsync();
        return 1;
    }
}