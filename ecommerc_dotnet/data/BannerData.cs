using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class BannerData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IWebHostEnvironment _host;
    public BannerData(AppDbContext dbContext,
        IConfig configuration,
        IWebHostEnvironment host
        )
    {
        _dbContext = dbContext;
        _config = configuration;
        _host = host;
    }
    
    public async Task<BannerResponseDto?> getBanner(
        Guid id)
    {
        try
        {
            return  await  _dbContext.Banner
                    .Include(st=>st.store)
                    .AsNoTracking()
                    .Where(ba=>ba.id==id)
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.id,
                        create_at = ba.create_at,
                        end_at = ba.end_at,
                        image = _config.getKey("url_file") + ba.image,
                        store_id = ba.store_id
                    }) 
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }
    
    
    public async Task<BannerResponseDto?> getBanner(
        Guid store_id,
        Guid banner_id)
    {
        try
        {
            return  await  _dbContext.Banner
                    .Include(st=>st.store)
                    .AsNoTracking()
                    .Where(ba=>ba.id==banner_id && ba.store_id==store_id)
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.id,
                        create_at = ba.create_at,
                        end_at = ba.end_at,
                        image =  ba.image,
                        store_id = ba.store_id
                    }) 
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }
    
 public async Task<List<BannerResponseDto>?> getBanner(
        Guid id,
        int pageNumber,
        int pageSize=25)
    {
        try
        {
            return  await  _dbContext.Banner
                    .Include(st=>st.store)
                    .AsNoTracking()
                    .Where(ba=>ba.store_id==id)
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.id,
                        create_at = ba.create_at,
                        end_at = ba.end_at,
                        image = _config.getKey("url_file") + ba.image,
                        store_id = ba.store_id
                    })
                    .Skip((pageNumber - 1) * pageSize)
                    .Take(pageSize)
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }

  
  

    public async Task<BannerResponseDto?> addNewBanner(
        DateTime end_at,
        string image_path,
        Guid store_id)
    {
        try
        {
            var id = clsUtil.generateGuid();
            var categoryObj = new Bannel
            {
                id = id,
                end_at = end_at,
                create_at = DateTime.Today,
                image = image_path,
                store_id = store_id
            };
            await _dbContext.Banner.AddAsync(categoryObj);
            await _dbContext.SaveChangesAsync();
            return await getBanner(id);
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new banner " + ex.Message);
            clsUtil.deleteFile(image_path, _host);
            return null;
        }
    }
    
    public async Task<bool?> deleteBanner(
        Guid banner_id)
    {
        try
        {
            var result = await _dbContext.Banner.FindAsync(banner_id);

            if (result == null)
            {
                return false;
            }

            _dbContext.Remove(result);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new banner " + ex.Message);
            return false;
        }
    }

    
    
}