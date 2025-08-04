using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class VarientRepository : IVarientRepository
{
    private readonly AppDbContext _context;

    public VarientRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Varient>> getAllAsync(int page, int length)
    {
        return await _context
            .Varients
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Varient entity)
    {
        await _context.Varients.AddAsync(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Varient entity)
    {
        _context.Varients.Update(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context
            .Varients
            .Where(i => i.Id == id)
            .ExecuteDeleteAsync();
        return 1;
    }

    public async Task<Varient?> getVarient(Guid id)
    {
        return await _context
            .Varients
            .FindAsync(id);
    }

    public async Task<int> getVarientCount()
    {
        return await _context
            .Varients
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Id == id);
    }

    public async Task<bool> isExist(string name)
    {
        return await _context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Name == name); 
    }

    public async Task<bool> isExist(string name, Guid id)
    {
        return await _context
            .Varients
            .AsNoTracking()
            .AnyAsync(i => i.Name == name && i.Id != id); 
    }
}