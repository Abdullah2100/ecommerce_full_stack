using ecommerc_dotnet.context;
using ecommerc_dotnet.domain.interfaces;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class Repository<T> : IRepository<T> where T : class
{
    private readonly AppDbContext _context;

    public Repository(AppDbContext context)
    {
        _context = context;
    }

    public virtual async Task<IEnumerable<T>> getAllAsync()
    {
        return await _context.Set<T>()
            .OrderDescending()
            .AsNoTracking()
            .ToListAsync();
    }

    public virtual async Task<IEnumerable<T>> getAllAsync(int page, int length)
    {
        IQueryable<T> query = _context.Set<T>();
        return await query
                .OrderDescending()
                .AsNoTracking()
                .Skip((page - 1) * length)
                .Take(length)
                .ToListAsync()
            ;
    }


    public async Task<int> addAsync(T entity)
    {
        await _context.Set<T>().AddAsync(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task updateAsync(T entity)
    {
        _context.Set<T>().Update(entity);
        await _context.SaveChangesAsync();
    }

    public virtual async Task deleteAsync(Guid id)
    {
        var entity = await _context.Set<T>().FindAsync(id);
        if (entity != null)
        {
            _context.Set<T>().Remove(entity);
        }
    }
}