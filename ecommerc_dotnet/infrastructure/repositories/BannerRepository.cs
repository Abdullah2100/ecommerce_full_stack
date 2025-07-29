using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class BannerRepository:IBannerRepository
{
    private readonly AppDbContext _context;

    public BannerRepository(AppDbContext context)
    {
        _context = context;
    }
    public async Task<IEnumerable<Banner>> getAllAsync(int page, int length)
    {
        return await  _context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .AsNoTracking()
            .Skip((page-1)*length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Banner entity)
    {
        await _context
            .Banner
            .AddAsync(entity);
        return await _context
            .SaveChangesAsync();
    }

    public async Task<int> updateAsync(Banner entity)
    {
         _context
             .Banner
             .Update(entity);
        return await _context
            .SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
       await _context
           .Banner
           .Where(ba => ba.Id == id)
           .ExecuteDeleteAsync();
       return await _context
           .SaveChangesAsync(); 
    }

    public async Task<Banner?> getBanner(Guid id)
    {
     return   await _context 
            .Banner
            .FindAsync(id); 
    }

    public async Task<Banner?> getBanner(Guid id, Guid storeId)
    {
        return   await _context
            .Banner
            .AsNoTracking()
            .FirstOrDefaultAsync(ba=>ba.Id==id&&ba.StoreId==storeId);
    }

    public async Task<List<Banner>> getBanners(Guid id, int pageNumber, int pageSize)
    {
        return await  _context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .Where(ba=>ba.StoreId==id)
            .AsNoTracking()
            .Skip((pageNumber-1)*pageSize)
            .Take(pageSize)
            .ToListAsync();
 
    }

    public async Task<List<Banner>> getBanners(int pageNumber, int pageSize)
    {
        return await  _context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .AsNoTracking()
            .Skip((pageNumber-1)*pageSize)
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<List<Banner>> getBanners(int randomLenght)
    {
        return await  _context.Banner
            .OrderBy(ba=>ba.Id)
            .AsNoTracking()
            .Take(randomLenght)
            .ToListAsync();
    }
}