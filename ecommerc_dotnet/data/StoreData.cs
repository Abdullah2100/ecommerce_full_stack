using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

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


    public StoreResponseDto? getStoreByUser(Guid id,bool isFullUrl=true)
    {
        try
        {
            return _dbContext.Store
                .AsNoTracking()
                .Where(st => st.user_id == id)
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaper_image =isFullUrl? _config.getKey("url_file") +st.wallpaper_image: st.wallpaper_image,
                    small_image =isFullUrl? _config.getKey("url_file") + st.small_image:st.small_image,
                    created_at = st.created_at,
                }).FirstOrDefault();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id " + ex.Message);
            return null;
        }
    }

    public async Task<List<StoreResponseDto>>? getStore(int pageNumber,int pageSize=25)
    {
        try
        {
            return await _dbContext.Store
                .AsNoTracking()
                .Where(st=>st.isBlock==false)
                .OrderBy(st=>st.created_at)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(st => new StoreResponseDto
                {
                    id = st.id,
                    name = st.name,
                    wallpaper_image = _config.getKey("url_file") +st.wallpaper_image,
                    small_image = _config.getKey("url_file") + st.small_image,
                    created_at = st.created_at,
                }).ToListAsync();
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
            _dbContext.Address.AddAsync(new Address
            {
                id = clsUtil.generateGuid(),
                isCurrent = true,
                latitude = latitude,
                longitude = longitude,
                title = name,
                created_at = DateTime.Now,
                updated_at = null,
            });
            var storeId = clsUtil.generateGuid();
            _dbContext.Store.AddAsync(new Store
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
            await _dbContext.SaveChangesAsync();
            return getStoreByUser(user_id);
        }
        catch (Exception ex)
        {
            return null;
            Console.WriteLine("error creating new store " + ex.Message);
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
                        title =store.name 
                    });
                }
                
                await _dbContext.SaveChangesAsync();
                return getStoreByUser(user_id);
            }
            catch (Exception ex)
            {
                Console.WriteLine("error updating store " + ex.Message);
                return null;
            }
        }
        
    
}