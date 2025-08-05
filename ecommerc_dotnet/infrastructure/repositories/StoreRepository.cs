using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class StoreRepository : IStoreRepository
{
    private readonly AppDbContext _context;

    public StoreRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Store>> getAllAsync(int page, int length)
    {
        List<Store> stores = await _context
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
            store.Addresses = await _context
                .Address
                .AsNoTracking()
                .Where(ad => ad.OwnerId == store.Id)
                .ToListAsync();
        }

        return stores;
    }

    public async Task<int> addAsync(Store entity)
    {
        await _context.Stores.AddAsync(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Store entity)
    {
        _context.Stores.Update(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        Store? store = await _context.Stores.FindAsync(id);
        if (store == null) return 0;
        store.IsBlock = !store.IsBlock;
        return await _context.SaveChangesAsync();
    }

    public async Task<Store?> getStore(Guid id)
    {
        Store? store = await _context
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(st => st.Id == id);

        if (store is null) return null;


        store.Addresses = await _context
            .Address
            .AsNoTracking()
            .Where(ad => ad.OwnerId == store.Id)
            .ToListAsync();
        return store;
    }

    public async Task<Store?> getStoreByUserId(Guid id)
    {
        Store? store = await _context
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(st => st.UserId == id);

        if (store is null) return null;


        store.Addresses = await _context
            .Address
            .AsNoTracking()
            .Where(ad => ad.OwnerId == store.Id)
            .ToListAsync();
        return store;
    }

    public async Task<int> getStoresCount()
    {
        return await _context
            .Stores
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<bool> isExist(string name)
    {
        return await _context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name);
    }

    public async Task<bool> isExist(string name, Guid id)
    {
        return await _context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name&& st.Id != id);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _context
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Id == id);
    }

    public async Task<bool> isExist(Guid id, Guid subCategoryId)
    {
        return await _context
            .Stores
            .Include(st => st.SubCategories)
            .AsNoTracking()
            .AnyAsync(st =>
                st.SubCategories != null &&
                st.Id == id &
                st.SubCategories.FirstOrDefault(sc => sc.Id == subCategoryId) != null);
    }
}