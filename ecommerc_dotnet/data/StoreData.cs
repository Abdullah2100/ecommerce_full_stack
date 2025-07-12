using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Internal;

namespace ecommerc_dotnet.data;

public class StoreData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;

    public StoreData(AppDbContext dbContext, IConfig config)
    {
        _dbContext = dbContext;
        _config = config;
    }


    public async Task<StoreResponseDto?> getStoreByUser(Guid userId)
    {
        try
        {
            return await _dbContext.Stores
                .Where(st => st.userId == userId)
                .AsNoTracking()
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaperImage = _config.getKey("url_file") + st.wallpaperImage,
                    smallImage = _config.getKey("url_file") + st.smallImage,
                    createdAt = st.createdAt,
                    userId = st.userId,
                    isBlocked = st.isBlock,
                    longitude = _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                        _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.longitude,
                    latitude =_dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                        _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.latitude, 
                })
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
        }
    }

    public async Task<StoreResponseDto?> getStoreById(Guid id)
    {
        try
        {
            return await _dbContext.Stores
                .Where(st => st.id == id)
                .AsNoTracking()
                .OrderByDescending(st => st.createdAt)
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaperImage = _config.getKey("url_file") + st.wallpaperImage,
                    smallImage = _config.getKey("url_file") + st.smallImage,
                    createdAt = st.createdAt,
                    userId = st.userId,
                    longitude = _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                        _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.longitude,
                    latitude =_dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                        _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.latitude, 
                })
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
        }
    }


    public async Task<List<AddressResponseDto>> getStoreAddress(
        Guid id,
        int pageNumber,
        int pageSize = 20
        )
    {
        try
        {
            return await _dbContext.Address
                .Where(ad => ad.ownerId == id)
                .AsNoTracking()
                .OrderByDescending(st => st.createdAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(ad => new AddressResponseDto
                {
                    id = ad.id,
                    isCurrent = ad.isCurrent,
                    longitude = ad.longitude,
                    latitude = ad.latitude,
                    title = ad.title
                })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return new  List<AddressResponseDto>();
        }
    }


    /*
        public static StoreResponseDto? getStoreByUser(Guid id,AppDbContext dbContext,IConfigurationServices config)
        {
            try
            {
                return       (from st in dbContext.Store
                        join ad in dbContext.Address on st.id equals ad.id
                        where st.userId == id
                        select new StoreResponseDto
                        {
                            id = st.id,
                            name = st.name,
                            wallpaperImage = config.getKey("url_file") +st.wallpaperImage,
                            smallImage = config.getKey("url_file") + st.smallImage,
                            createdAt = st.createdAt,
                            latitude = ad.latitude,
                            longitide = ad.longitude,
                            user = UserData.getUser(st.userId,dbContext,config)
                        }
                    )
                    .AsNoTracking()
                    .OrderByDescending(st=>st.createdAt)
                    .FirstOrDefault();
            }
            catch (Exception ex)
            {
                Console.WriteLine("error from getting store by id " + ex.Message);
                return null;
            }
        }
    */

    public async Task<List<StoreResponseDto>?> getStore(int pageNumber, int pageSize = 25)
    {
        try
        {
            return
                await _dbContext.Stores
                    .Include(st=>st.user)
                    .AsNoTracking()
                    .AsSplitQuery()
                    .OrderByDescending(st => st.createdAt)
                    .Skip((pageNumber - 1) * pageSize)
                    .Take(pageSize)
                    .Select(st => new StoreResponseDto
                    {
                        id = st.id,
                        name = st.name,
                        wallpaperImage = _config.getKey("url_file") + st.wallpaperImage,
                        smallImage = _config.getKey("url_file") + st.smallImage,
                        createdAt = st.createdAt,
                        userId = st.userId,
                        isBlocked = st.isBlock,
                        userName = st.user.name,
                        longitude = _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                            _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.longitude,
                        latitude =_dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)==null?null:
                            _dbContext.Address.FirstOrDefault(ad=>ad.ownerId==st.id)!.latitude, 
                    })
                    .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
        }
    }

    public async Task<int> getStorePages()
    {
        try
        {
            int storesSize = await _dbContext.Stores.CountAsync();
            if (storesSize == 0) return 0;
            return (int)Math.Ceiling((double)storesSize / 25);
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return 0;
        }
    }

    public async Task<bool> isExist(string name)
    {
        try
        {
            return await _dbContext
                .Stores
                .AsNoTracking()
                .FirstOrDefaultAsync(st => st.name == name) !=  null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }

    public async Task<bool> isExist(Guid storeId)
    {
        try
        {
            return await _dbContext
                .Stores
                .AsNoTracking()
                .FirstOrDefaultAsync(st => st.id == storeId) !=  null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }
    
    public async Task<bool> isExist(Guid storeId,Guid subcategoryId)
    {
        try
        {
            return await _dbContext.Stores
                .Include(st=>st.SubCategories)
                .AsNoTracking()
                .AsSplitQuery()
                .FirstOrDefaultAsync(st => st.id == storeId&&
                                           st.SubCategories!= null&& 
                                           st.SubCategories
                                               .FirstOrDefault(sbu=>sbu.id==subcategoryId)!= null) !=  null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }



    public async Task<StoreResponseDto?> createStore(
        string name,
        string wallpaperImage,
        string smallImage,
        Guid userId,
        decimal longitude,
        decimal latitude
    )
    {
        try
        {
            Guid storeId = clsUtil.generateGuid();
            await _dbContext.Stores.AddAsync(new Store
            {
                id = storeId,
                name = name,
                wallpaperImage = wallpaperImage,
                smallImage = smallImage,
                isBlock = true,
                userId = userId,
                createdAt = DateTime.Now,
                updatedAt = null,
            });

            await _dbContext.Address.AddAsync(new Address
            {
                id = clsUtil.generateGuid(),
                isCurrent = true,
                latitude = latitude,
                longitude = longitude,
                title = name,
                createdAt = DateTime.Now,
                updatedAt = null,
                ownerId = storeId
            });

            await _dbContext.SaveChangesAsync();
            return await getStoreByUser(userId);
        }
        catch (Exception ex)
        {
            Console.WriteLine("error creating new store " + ex.Message);
            return null;
        }
    }


    public async Task<bool?> updateStoreStatus(Guid storeId)
    {
        try
        {
            Store? store = await _dbContext.Stores.FindAsync( storeId);
            if (store is null) return false;

            store.isBlock = !store.isBlock;


            await _dbContext.SaveChangesAsync();
            return store.isBlock;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error updating store " + ex.Message);
            return null;
        }
    }

    public async Task<StoreResponseDto?> updateStore(
        string? name,
        string? wallpaperImage,
        string? smallImage,
        Guid userId,
        decimal? longitude,
        decimal? latitude
    )
    {
        try
        {
            Store? store = await _dbContext.Stores.FirstOrDefaultAsync(st => st.userId == userId);

            if (store is null)
                return null;
            store.smallImage = smallImage ?? store.smallImage;
            store.wallpaperImage = wallpaperImage ?? store.wallpaperImage;
            store.name = name ?? store.name;

            if (longitude !=  null && latitude !=  null)
            {
                await _dbContext
                    .Address
                    .Where(ad => ad.ownerId == store.id)
                    .ExecuteDeleteAsync();
                
                _dbContext.Address.Add(new Address
                {
                    id = clsUtil.generateGuid(),
                    isCurrent = true,
                    latitude = (decimal)latitude,
                    longitude = (decimal)longitude,
                    ownerId = store.id,
                    title = store.name
                });
            }

            await _dbContext.SaveChangesAsync();
            return await getStoreByUser(userId);
        }
        catch (Exception ex)
        {
            Console.WriteLine("error updating store " + ex.Message);
            return null;
        }
    }
}