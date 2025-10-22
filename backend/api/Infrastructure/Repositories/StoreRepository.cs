using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.domain.entity;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class StoreRepository(AppDbContext context) : IStoreRepository
{
     public void  add(Store entity)
    {
         context.Stores.Add(entity);
    }

    public void update(Store entity)
    {
        context.Stores.Update(entity);
    }

    public void  delete(Guid id)
    {
        Store? store =  context.Stores.Find(id);
        if (store == null) throw new ArgumentNullException();
        store.IsBlock = !store.IsBlock;
    }

    public async Task<Store?> getStore(Guid id)
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

    public async Task<Store?> getStoreByUserId(Guid id)
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

    public async Task<List<Store>> getStores(int page, int length)
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

    public async Task<int> getStoresCount()
    {
        return await context
            .Stores
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<bool> isExist(string name)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name);
    }

    public async Task<bool> isExist(string name, Guid id)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name&& st.Id != id);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Id == id);
    }

    public async Task<bool> isExist(Guid id, Guid subCategoryId)
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