using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class BannerRepository(AppDbContext context) : IBannerRepository
{
    public async Task<IEnumerable<Banner>> getAllAsync(int page, int length)
    {
        return await  context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .AsNoTracking()
            .Skip((page-1)*length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Banner entity)
    {
        await context
            .Banner
            .AddAsync(entity);
        return await context
            .SaveChangesAsync();
    }

    public async Task<int> updateAsync(Banner entity)
    {
         context
             .Banner
             .Update(entity);
        return await context
            .SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
       await context
           .Banner
           .Where(ba => ba.Id == id)
           .ExecuteDeleteAsync();
       return await context
           .SaveChangesAsync(); 
    }

    public async Task<Banner?> getBanner(Guid id)
    {
     return   await context 
            .Banner
            .FindAsync(id); 
    }

    public async Task<Banner?> getBanner(Guid id, Guid storeId)
    {
        return   await context
            .Banner
            .AsNoTracking()
            .FirstOrDefaultAsync(ba=>ba.Id==id&&ba.StoreId==storeId);
    }

    public async Task<List<Banner>> getBanners(Guid id, int pageNumber, int pageSize)
    {
        return await  context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .Where(ba=>ba.StoreId==id)
            .AsNoTracking()
            .Skip((pageNumber-1)*pageSize)
            .Take(pageSize)
            .ToListAsync();
 
    }

    public async Task<List<Banner>> getBanners(int pageNumber, int pageSize)
    {
        return await  context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .AsNoTracking()
            .Skip((pageNumber-1)*pageSize)
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<List<Banner>> getBanners(int randomLenght)
    {
        return await  context.Banner
            .OrderBy(ba=>ba.Id)
            .AsNoTracking()
            .Take(randomLenght)
            .ToListAsync();
    }
}