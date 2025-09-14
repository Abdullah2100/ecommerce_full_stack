using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class CategoryRepository(AppDbContext context) : ICategoryRepository
{
    public async Task<IEnumerable<Category>> getAllAsync(int page, int length)
    {
        return await context
            .Categories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<int> addAsync(Category entity)
    {
        await context.Categories.AddAsync(entity);
        return await context.SaveChangesAsync();
    }

    public Task<int> updateAsync(Category entity)
    {
        context.Categories.Update(entity);
        return context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context.Categories.Where(ca => ca.Id == id).ExecuteDeleteAsync();
        return 1;
    }

    public async Task<Category?> getCategory(Guid id)
    {
        return await context.Categories.FindAsync(id);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Id == id);
    }

    public async Task<bool> isExist(string name)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name);
    }

    public async Task<bool> isExist(string name,Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name && e.Id != id);
    }
}