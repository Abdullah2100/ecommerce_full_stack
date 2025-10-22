using ecommerc_dotnet.application;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class BannerRepository(AppDbContext context) : IBannerRepository
{

    public void  add(Banner entity)
    {
         context
            .Banner
            .AddAsync(entity);
    }

    public void update(Banner entity)
    {
         context
             .Banner
             .Update(entity);
    }

    public void  deleteAsync(Guid id)
    {
       var banners =  context
           .Banner
           .Where(ba => ba.Id == id)
           .ToListAsync();
       
       context.RemoveRange(banners);
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