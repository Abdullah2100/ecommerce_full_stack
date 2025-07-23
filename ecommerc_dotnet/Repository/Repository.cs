using ecommerc_dotnet.context;
using Google;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.di.Repository;

public class Repository<T> : IRepository<T> where T : class
{
    private readonly AppDbContext _context;

    public Repository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<T>> GetAllAsync(string[]? includes = null)
    {
        IQueryable<T> query = _context.Set<T>();
        if (includes != null)
            foreach (var include in includes)
            {
                query = query.Include(include);
            }

        return await query
            .OrderDescending()
            .AsNoTracking()
            .ToListAsync();
    }

    public async Task<IEnumerable<T>> GetAllAsync(int page, int length, string[]? includes = null)
    {
        IQueryable<T> query = _context.Set<T>();
        if (includes != null)
        {  foreach (var include in includes)
            {
                query = query.Include(include);
            }
            query.AsSplitQuery();
        }

        return await query
                .OrderDescending()
                .AsNoTracking()
                .Skip((page - 1) * length)
                .Take(length)
                .ToListAsync()
            ;
    }

    public async Task<T?> GetByIdAsync(Guid id, string[]? includes = null)
    {
        IQueryable<T> query = _context.Set<T>();
        if (includes != null)
        {foreach (var include in includes)
            {
                query = query.Include(include);
            }
            query.AsSplitQuery();
        }

        return await query
            .AsNoTracking()
            .FirstOrDefaultAsync();
    }

    public async Task AddAsync(T entity)
    {
        await _context.Set<T>().AddAsync(entity);
    }

    public async Task UpdateAsync(T entity)
    {
        _context.Set<T>().Update(entity);
    }

    public async Task DeleteAsync(Guid id)
    {
        var entity = await _context.Set<T>().FindAsync(id);
        if (entity != null)
        {
            _context.Set<T>().Remove(entity);
        }
    }
}