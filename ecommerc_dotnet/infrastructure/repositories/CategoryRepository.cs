using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class CategoryRepository:ICategoryRepository
{
    private readonly AppDbContext _context;

    public CategoryRepository(AppDbContext context)
    {
        _context = context;
    }
    
    public async Task<IEnumerable<Category>> getAllAsync(int page, int length)
    {
        return await _context
            .Categories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<int> addAsync(Category entity)
    {
        await _context.Categories.AddAsync(entity);
        return await _context.SaveChangesAsync();
    }

    public Task<int> updateAsync(Category entity)
    {
        _context.Categories.Update(entity);
        return _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context.Categories.Where(ca => ca.Id == id).ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public async Task<Category?> getCategory(Guid id)
    {
        return await _context.Categories.FindAsync(id);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Id == id);
    }

    public async Task<bool> isExist(string name)
    {
        return await _context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name);
    }
}