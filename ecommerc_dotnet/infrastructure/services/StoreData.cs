using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Internal;

namespace ecommerc_dotnet.data;

public class StoreData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IUnitOfWork _unitOfWork;

    public StoreData(
        AppDbContext dbContext,
        IConfig config,
        IUnitOfWork unitOfWork)
    {
        _dbContext = dbContext;
        _config = config;
        _unitOfWork = unitOfWork;
    }


    public async Task<StoreDto?> getStoreByUser(Guid userId)
    {
        Store? store = await _dbContext
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(s => s.UserId == userId);
        if (store == null) return null;

        Address? address = await _dbContext
            .Address
            .AsNoTracking()
            .FirstOrDefaultAsync(ad => ad.OwnerId == store.Id);

        if (address != null)
            store.Addresses = new List<Address> { address };

        return store.toDto(_config.getKey("url_file"));
    }

    public async Task<StoreDto?> getStoreById(Guid id)
    {
        Store? store = await _dbContext
            .Stores
            .Include(st => st.user)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(st => st.Id == id);

        if (store == null) return null;

        Address? address = await _dbContext
            .Address
            .AsNoTracking()
            .FirstOrDefaultAsync(ad => ad.OwnerId == id);

        if (address != null)
            store.Addresses = new List<Address> { address };

        return store.toDto(_config.getKey("url_file"));
    }


    public async Task<List<AddressDto>> getStoreAddress(
        Guid id,
        int pageNumber,
        int pageSize = 20
    )
    {
        Store? store = await _dbContext.Stores.FindAsync(id);
        if (store == null) return new List<AddressDto>();

        var addresss = await _dbContext
            .Address
            .AsNoTracking()
            .Where(ad => ad.OwnerId == id)
            .ToListAsync();

        return addresss.Select(ad => ad.toDto()).ToList();
    }


    public async Task<List<StoreDto>?> getStore(int pageNumber, int pageSize = 25)
    {
        IEnumerable<Store> stores = await _unitOfWork
            .StoreRepository
            .getAllAsync(pageNumber, pageSize, ["Users"]);

        var storeList = stores.ToList();

        foreach (Store store in storeList)
        {
            var addresss = await _dbContext
                .Address
                .AsNoTracking()
                .Where(ad => ad.OwnerId == store.Id)
                .ToListAsync();
            store.Addresses = addresss;
        }

        return storeList.Select(st => st.toDto(_config.getKey("url_file"))).ToList();
    }

    public async Task<int> getStorePages()
    {
        int storesSize = await _dbContext
            .Stores
            .AsNoTracking()
            .CountAsync();
        if (storesSize == 0) return 0;
        return (int)Math.Ceiling((double)storesSize / 25);
    }

    public async Task<bool> isExist(string name)
    {
        return await _dbContext
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Name == name);
    }

    public async Task<bool> isExist(Guid storeId)
    {
        return await _dbContext
            .Stores
            .AsNoTracking()
            .AnyAsync(st => st.Id == storeId);
    }

    public async Task<bool> isExist(Guid storeId, Guid subcategoryId)
    {
        try
        {
            return await _dbContext.Stores
                .Include(st => st.SubCategories)
                .AsNoTracking()
                .AsSplitQuery()
                .AnyAsync(st => st.Id == storeId &&
                                st.SubCategories != null &&
                                st.SubCategories
                                    .FirstOrDefault(sbu => sbu.Id == subcategoryId) != null);
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }


    public async Task<StoreDto?> createStore(
        string name,
        string wallpaperImage,
        string smallImage,
        Guid userId,
        decimal longitude,
        decimal latitude
    )
    {
        Guid id = clsUtil.generateGuid();

        Store store = new Store
        {
            Id = id,
            Name = name,
            WallpaperImage = wallpaperImage,
            SmallImage = smallImage,

            IsBlock = true,
            UserId = userId,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
        };

        Address address = new Address
        {
            Id = clsUtil.generateGuid(),
            IsCurrent = true,
            Latitude = latitude,
            Longitude = longitude,
            Title = name,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            OwnerId = id
        };

        await _unitOfWork.StoreRepository.addAsync(store);
        await _unitOfWork.AddressRepository.addAsync(address);

        int result = await _unitOfWork.Complate();
        if (result == 0) return null;

        return await getStoreById(id);
    }


    public async Task<bool?> updateStoreStatus(Guid storeId)
    {
        Store? store = await _dbContext.Stores.FindAsync(storeId);
        if (store is null) return false;

        store.IsBlock = !store.IsBlock;


        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return null;

        return true;
    }

    public async Task<StoreDto?> updateStore(
        string? name,
        string? wallpaperImage,
        string? smallImage,
        Guid userId,
        decimal? longitude,
        decimal? latitude
    )
    {
        Store? store = await _dbContext.Stores.FirstOrDefaultAsync(st => st.UserId == userId);

        if (store is null) return null;
        
        store.SmallImage = smallImage ?? store.SmallImage;
        store.WallpaperImage = wallpaperImage ?? store.WallpaperImage;
        store.Name = name ?? store.Name;
        store.UpdatedAt = DateTime.Now;

        if (longitude is not  null && latitude is not  null)
        {
            Address? address = await _dbContext
                .Address
                .FirstOrDefaultAsync(ad => ad.OwnerId == store.Id);

            if (address is not  null)
            {
                address.Title = store.Name;
                address.UpdatedAt = DateTime.Now;
                address.Longitude = (decimal)longitude;
                address.Latitude = (decimal)latitude;
            }
            
        }

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return null;
        return await getStoreByUser(userId);
    }
}