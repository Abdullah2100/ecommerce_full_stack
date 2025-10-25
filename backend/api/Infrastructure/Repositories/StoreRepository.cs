using api.domain.entity;
using api.domain.Interface;
using ecommerc_dotnet.application;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class StoreRepository(AppDbContext context) : IStoreRepository
{
     public void  Add(Store entity)
    {
         context.Stores.Add(entity);
    }

    public void Update(Store entity)
    {
        context.Stores.Update(entity);
    }

    public void  Delete(Guid id)
    {
        Store? store =  context.Stores.Find(id);
        if (store == null) throw new ArgumentNullException();
        store.IsBlock = !store.IsBlock;
    }

    public async Task<Store?> GetStore(Guid id)
    {
        Store? store = await context
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(st => st.Id == id);

        if (store is null) return null;


        store.Addresses = await context
            .Address
            .AsNoTracking()
            .Where(ad => ad.OwnerId == store.Id)
            .ToListAsync();
        return store;
    }

    public async Task<Store?> GetStoreByUserId(Guid id)
    {
        Store? store = await context
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(st => st.UserId == id);

        if (store is null) return null;


        store.Addresses = await context
            .Address
            .AsNoTracking()
            .Where(ad => ad.OwnerId == store.Id)
            .ToListAsync();
        return store;
    }

    public async Task<List<Store>> GetStores(int page, int length)
    {
        List<Store> stores = await context
            .Stores
            .Include(st => st.user)
            .Include(st => st.SubCategories)
            .AsSplitQuery()
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();

        if (stores.Count <= 0) return new List<Store>();


        foreach (var store in stores)
        {
            store.Addresses = await context
                .Address
                .AsNoTracking()
                .Where(ad => ad.OwnerId == store.Id)
                .ToListAsync();
        }

        return stores;
    }

    public async Task<int> GetStoresCount()
    {
        return await context
            .Stores
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<bool> IsExist(string name)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name);
    }

    public async Task<bool> IsExist(string name, Guid id)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name&& st.Id != id);
    }

    public async Task<bool> IsExist(Guid id)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Id == id);
    }

    public async Task<bool> IsExist(Guid id, Guid subCategoryId)
    {
        return await context
            .Stores
            .Include(st => st.SubCategories)
            .AsNoTracking()
            .AnyAsync(st =>
                st.SubCategories != null &&
                st.Id == id &
                st.SubCategories.FirstOrDefault(sc => sc.Id == subCategoryId) != null);
    }
}