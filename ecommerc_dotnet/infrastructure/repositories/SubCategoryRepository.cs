using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.data;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class SubCategoryRepository: ISubCategoryRepository
{
    private readonly AppDbContext _context;

    public SubCategoryRepository(AppDbContext context)
    {
        _context = context;
    }
    public async Task<SubCategory?> getSubCategory(Guid id)
    {
       return await _context.SubCategories.FindAsync(id);
    }

    public async Task<List<SubCategory>> getSubCategories(
        Guid storeId,
        int  pageNumber,
        int pageSize
        )
    {
        return await _context
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
        return await _context
            .SubCategories
            .AsNoTracking()
            .Where(su => su.StoreId == storeId)
            .CountAsync();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _context.SubCategories.FindAsync(id)!=null;

    }

    public async Task<bool> isExist(Guid storeId, string name)
    {
        return await _context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Name == name);
    }

    public async Task<bool> isExist(Guid storeId, Guid id)
    {
        return await _context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Id == id);

    }

    public async Task<IEnumerable<SubCategory>> getAllAsync(int page, int length)
    {
        return await _context
            .SubCategories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .OrderDescending()
            .Take(length)
            .ToListAsync(); 
    }

    public async Task<int> addAsync(SubCategory entity)
    {
        await _context.SubCategories.AddAsync(entity);
        return  await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(SubCategory entity)
    {
        _context.SubCategories.Update(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context.SubCategories.Where(su => su.Id == id)
            .ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }
}