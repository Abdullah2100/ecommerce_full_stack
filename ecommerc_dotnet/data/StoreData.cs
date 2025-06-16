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
            return await _dbContext.Store
                .AsNoTracking()
                .Where(st => st.user_id == userId)
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaper_image = _config.getKey("url_file") + st.wallpaper_image,
                    small_image = _config.getKey("url_file") + st.small_image,
                    created_at = st.created_at,
                    user_id = st.user_id,
                    isBlocked = st.isBlock
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
            return await _dbContext.Store
                .AsNoTracking()
                .Where(st => st.id == id)
                .OrderByDescending(st => st.created_at)
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaper_image = _config.getKey("url_file") + st.wallpaper_image,
                    small_image = _config.getKey("url_file") + st.small_image,
                    created_at = st.created_at,
                    user_id = st.user_id
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
                .AsNoTracking()
                .Where(ad => ad.owner_id == id)
                .OrderByDescending(st => st.created_at)
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
                        where st.user_id == id
                        select new StoreResponseDto
                        {
                            id = st.id,
                            name = st.name,
                            wallpaper_image = config.getKey("url_file") +st.wallpaper_image,
                            small_image = config.getKey("url_file") + st.small_image,
                            created_at = st.created_at,
                            latitude = ad.latitude,
                            longitide = ad.longitude,
                            user = UserData.getUser(st.user_id,dbContext,config)
                        }
                    )
                    .AsNoTracking()
                    .OrderByDescending(st=>st.created_at)
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
                await _dbContext.Store
                    .Include(st=>st.user)
                    .AsNoTracking()
                    .OrderByDescending(st => st.created_at)
                    .Skip((pageNumber - 1) * pageSize)
                    .Take(pageSize)
                    .Select(st => new StoreResponseDto
                    {
                        id = st.id,
                        name = st.name,
                        wallpaper_image = _config.getKey("url_file") + st.wallpaper_image,
                        small_image = _config.getKey("url_file") + st.small_image,
                        created_at = st.created_at,
                        user_id = st.user_id,
                        isBlocked = st.isBlock,
                        userName = st.user.name
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
            int storesSize = await _dbContext.Store.CountAsync();
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
            return await _dbContext.Store.AsNoTracking()
                .FirstOrDefaultAsync(st => st.name == name) != null;
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
            return await _dbContext.Store.AsNoTracking()
                .FirstOrDefaultAsync(st => st.id == storeId) != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }
    
    public async Task<bool> isExist(Guid storeId,Guid subCategory_id)
    {
        try
        {
            return await _dbContext.Store
                .Include(st=>st.SubCategories)
                .AsNoTracking()
                .FirstOrDefaultAsync(st => st.id == storeId&& st.SubCategories.FirstOrDefault(sbu=>sbu.id==subCategory_id)!=null) != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return false;
        }
    }



    public async Task<StoreResponseDto?> createStore(
        string name,
        string wallpaper_image,
        string small_image,
        Guid user_id,
        decimal longitude,
        decimal latitude
    )
    {
        try
        {
            Guid storeId = clsUtil.generateGuid();
            await _dbContext.Store.AddAsync(new Store
            {
                id = storeId,
                name = name,
                wallpaper_image = wallpaper_image,
                small_image = small_image,
                isBlock = true,
                user_id = user_id,
                created_at = DateTime.Now,
                updated_at = null,
            });

            await _dbContext.Address.AddAsync(new Address
            {
                id = clsUtil.generateGuid(),
                isCurrent = true,
                latitude = latitude,
                longitude = longitude,
                title = name,
                created_at = DateTime.Now,
                updated_at = null,
                owner_id = storeId
            });

            await _dbContext.SaveChangesAsync();
            return await getStoreByUser(user_id);
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
            Store? store = await _dbContext.Store.FindAsync( storeId);
            if (store == null) return false;

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
        string? wallpaper_image,
        string? small_image,
        Guid user_id,
        decimal? longitude,
        decimal? latitude
    )
    {
        try
        {
            Store? store = await _dbContext.Store.FirstOrDefaultAsync(st => st.user_id == user_id);

            store.small_image = small_image ?? store.small_image;
            store.wallpaper_image = wallpaper_image ?? store.wallpaper_image;
            store.name = name ?? store.name;

            if (longitude != null && latitude != null)
            {
                IQueryable<Address>? address = _dbContext.Address.Where(ad => ad.owner_id == store.id);
                _dbContext.Address.RemoveRange(address);
                _dbContext.Address.Add(new Address
                {
                    id = clsUtil.generateGuid(),
                    isCurrent = true,
                    latitude = (decimal)latitude,
                    longitude = (decimal)longitude,
                    owner_id = store.id,
                    title = store.name
                });
            }

            await _dbContext.SaveChangesAsync();
            return await getStoreByUser(user_id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("error updating store " + ex.Message);
            return null;
        }
    }
}