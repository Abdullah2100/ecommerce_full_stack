using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
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

    public void  add(Varient entity)
    {
         context.Varients.AddAsync(entity);
    }

    public void update(Varient entity)
    {
        context.Varients.Update(entity);
    }

    public void  deleteAsync(Guid id)
    {
      var variants=   context
            .Varients
            .Where(i => i.Id == id)
            .ToList();
      context.Varients.RemoveRange(variants);
    }

    public async Task<Varient?> getVarient(Guid id)
    {
        return await context
            .Varients
            .FindAsync(id);
    }

    public async Task<List<Varient>> getVarients(int page, int length)
    {
        return await context
            .Varients
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync(); 
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