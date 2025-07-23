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
                    .Where(ba=>ba.Id==id)
                    .AsNoTracking()
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.Id,
                        createdAt = ba.CreatedAt,
                        endAt = ba.EndAt,
                        image = _config.getKey("url_file") + ba.Image,
                        storeId = ba.StoreId
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
        Guid storeId,
        Guid banner_id)
    {
        try
        {
            return  await  _dbContext.Banner
                    .Where(ba=>ba.Id==banner_id && ba.StoreId==storeId)
                    .AsNoTracking()
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.Id,
                        createdAt = ba.CreatedAt,
                        endAt = ba.EndAt,
                        image =  ba.Image,
                        storeId = ba.StoreId
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
                    .Where(ba=>ba.StoreId==id)
                    .AsNoTracking()
                    .Select(ba=>new BannerResponseDto
                    {
                        id = ba.Id,
                        createdAt = ba.CreatedAt,
                        endAt = ba.EndAt,
                        image = _config.getKey("url_file") + ba.Image,
                        storeId = ba.StoreId
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
        DateTime endAt,
        string image,
        Guid storeId)
    {
        try
        {
            Guid id = clsUtil.generateGuid();
            Bannel categoryObj = new Bannel
            {
                Id = id,
                EndAt = endAt,
                CreatedAt = DateTime.Today,
                Image = image,
                StoreId = storeId
            };
            await _dbContext.Banner.AddAsync(categoryObj);
            await _dbContext.SaveChangesAsync();
            return await getBanner(id);
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new banner " + ex.Message);
            clsUtil.deleteFile(image, _host);
            return null;
        }
    }
    
    public async Task<bool> deleteBanner(
        Guid banner_id)
    {
        try
        {
            Bannel? result = await _dbContext.Banner.FindAsync(banner_id);

            if (result is null)
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

    
    public async Task<int?> getBanner()
    {
        try
        {
            return await _dbContext.Banner
                .Where(ban=>ban.EndAt>DateTime.Now)
                .AsNoTracking()
                .CountAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }

    
    
    public async Task<List<BannerResponseDto>?> getBanner(int pageSize)
    {
        try
        {
            return  await  _dbContext.Banner
                .AsNoTracking()
                .OrderBy(u=>Guid.NewGuid())
                .Select(ba=>new BannerResponseDto
                {
                    id = ba.Id,
                    createdAt = ba.CreatedAt,
                    endAt = ba.EndAt,
                    image = _config.getKey("url_file") + ba.Image,
                    storeId = ba.StoreId
                })
                .Take(pageSize)
                .ToListAsync(); 
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }

    
}