using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Internal;

namespace ecommerc_dotnet.data;

public class StoreData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfigurationServices _config;

    public StoreData(AppDbContext dbContext, IConfigurationServices config)
    {
        _dbContext = dbContext;
        _config = config;
    }


    public async Task<StoreResponseDto?> getStoreByUser(Guid id)
    {
        try
        {
            var result = await (from st in _dbContext.Store
                    join ad in _dbContext.Address on st.id equals ad.owner_id
                    where st.user_id == id
                    select new StoreResponseDto
                    {
                        id = st.id,
                        name = st.name,
                        wallpaper_image = _config.getKey("url_file") + st.wallpaper_image,
                        small_image = _config.getKey("url_file") + st.small_image,
                        created_at = st.created_at,
                        latitude = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                        longitide = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                        subcategory = st.SubCategories.Select(sub => new SubCategoryResponseDto
                        {
                            name = sub.name,
                            id = sub.id,
                        }).ToList(),
                        user = new UserInfoResponseDto
                        {
                            name = st.name,
                            phone = st.user.phone,
                            email = st.user.phone,
                            Id = st.user.ID,
                            thumbnail = st.user.thumbnail == null
                                ? null
                                : _config.getKey("url_file") + st.user.thumbnail,
                            address = _dbContext.Address.Where(add=>add.owner_id==st.user_id)
                                .Select(add=>new AddressResponseDto
                                {
                                    id=add.id,
                                    isCurrent = add.isCurrent,
                                    latitude=add.latitude,
                                    longitude = add.longitude,
                                    title = add.title
                                }).ToList(),
                            store = null
                        },
                    }
                )
                .AsNoTracking()
                .OrderByDescending(st => st.created_at)
                .FirstOrDefaultAsync();
            return result;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
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
    public async Task<List<StoreResponseDto>>? getStore(int pageNumber, int pageSize = 25)
    {
        try
        {
            return await (from st in _dbContext.Store
                    join ad in _dbContext.Address on st.id equals ad.owner_id
                    
                    select new StoreResponseDto
                    {
                        id = st.id,
                        name = st.name,
                        wallpaper_image = _config.getKey("url_file") + st.wallpaper_image,
                        small_image = _config.getKey("url_file") + st.small_image,
                        created_at = st.created_at,
                        latitude = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                        longitide = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                        subcategory = st.SubCategories.Select(sub => new SubCategoryResponseDto
                        {
                            name = sub.name,
                            id = sub.id,
                        }).ToList(),
                        user = new UserInfoResponseDto
                        {
                            name = st.name,
                            phone = st.user.phone,
                            email = st.user.phone,
                            Id = st.user.ID,
                            thumbnail = st.user.thumbnail == null
                                ? null
                                : _config.getKey("url_file") + st.user.thumbnail,
                            address = _dbContext.Address.Where(add=>add.owner_id==st.user_id)
                                .Select(add=>new AddressResponseDto
                                {
                                    id=add.id,
                                    isCurrent = add.isCurrent,
                                    latitude=add.latitude,
                                    longitude = add.longitude,
                                    title = add.title
                                }).ToList(),
                            store = null
                        },
                    }
                )
                .AsNoTracking()
            .OrderByDescending(st => st.created_at)
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
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
            var storeId = clsUtil.generateGuid();
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


    public async Task<bool> updateStoreStatus(Guid storeId)
    {
        try
        {
            var store = await _dbContext.Store.FirstOrDefaultAsync(st => st.id == storeId);

            store.isBlock = !store.isBlock;


            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error updating store " + ex.Message);
            return false;
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
            var store = await _dbContext.Store.FirstOrDefaultAsync(st => st.user_id == user_id);

            store.small_image = small_image ?? store.small_image;
            store.wallpaper_image = wallpaper_image ?? store.wallpaper_image;
            store.name = name ?? store.name;

            if (longitude != null && latitude != null)
            {
                var address = _dbContext.Address.Where(ad => ad.owner_id == store.id);
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