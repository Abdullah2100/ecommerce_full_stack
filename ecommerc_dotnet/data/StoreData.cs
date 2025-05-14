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
    
    

    public StoreResponseDto? getStore(Guid id)
    {
        try
        {
            return   _dbContext.Store
                .AsNoTracking()
                .Where(st => st.id == id&&st.isBlock==false)
                .Select(st => new StoreResponseDto
                {
                    id=st.id,
                    name = st.name,
                    wallpaper_image = _config.getKey("url_file")+st.wallpaper_image,
                    small_image = _config.getKey("url_file")+st.small_image,
                    created_at = st.created_at,
                }).FirstOrDefault();
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id "+ex.Message);
            return null;
        }
    }
    
    public async Task<bool>isExist (string name)
    {
        try
        {
            return await _dbContext.Store.AsNoTracking().FirstOrDefaultAsync(st => st.name == name && st.isBlock == false) != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("error from getting store by id "+ex.Message);
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
            return getStore(storeId);
        }
        catch (Exception ex)
        {
            return null;
            Console.WriteLine("error creating new store "+ex.Message);
        }
        
    }
    
    
}