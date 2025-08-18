using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class VarientRepository(AppDbContext context) : IVarientRepository
{
    public async Task<IEnumerable<Varient>> getAllAsync(int page, int length)
    {
        return await context
            .Varients
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Varient entity)
    {
        await context.Varients.AddAsync(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Varient entity)
    {
        context.Varients.Update(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context
            .Varients
            .Where(i => i.Id == id)
            .ExecuteDeleteAsync();
        return 1;
    }

    public async Task<Varient?> getVarient(Guid id)
    {
        return await context
            .Varients
            .FindAsync(id);
    }

    public async Task<int> getVarientCount()
    {
        return await context
            .Varients
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Id == id);
    }

    public async Task<bool> isExist(string name)
    {
        return await context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Name == name); 
    }

    public async Task<bool> isExist(string name, Guid id)
    {
        return await context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Name == name && i.Id != id); 
    }
}