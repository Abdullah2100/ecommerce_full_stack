using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class BannerData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IUnitOfWork _unitOfWork;

    public BannerData(AppDbContext dbContext,
        IConfig configuration,
        IWebHostEnvironment host,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _config = configuration;
        _unitOfWork = unitOfWork;
    }

    public async Task<BannerDto?> getBanner(
        Guid id)
    {
        Banner? banner = await _dbContext
            .Banner
            .FindAsync(id);

        return banner
            ?.toDto(_config.getKey("url_file"));
    }


    public async Task<BannerDto?> getBanner(
        Guid storeId,
        Guid bannerId)
    {
        Banner? banner = await _dbContext
            .Banner
            .AsNoTracking()
            .FirstOrDefaultAsync(ba => ba.Id == bannerId && ba.StoreId == storeId);
        return banner?.toDto(_config.getKey("url_file"));
    }
    
    

    public async Task<List<BannerDto>?> getBanners(
        Guid id,
        int pageNumber,
        int pageSize = 25)
    {
    return await  _dbContext.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .Where(ba=>ba.StoreId==id)
            .AsNoTracking()
            .Skip((pageNumber-1)*pageSize)
            .Take(pageSize)
            .Select(ad=>ad.toDto(_config.getKey("url_file")))
            .ToListAsync();
    }

    public async Task<List<BannerDto>?> getRandomBanners(
        int length)
    {
        return await  _dbContext.Banner
            .OrderBy(o=>o.Id)
            .AsNoTracking()
            .Take(length)
            .Select(ad=>ad.toDto(_config.getKey("url_file")))
            .ToListAsync();
    }


    public async Task<BannerDto?> addNewBanner(
        DateTime endAt,
        string image,
        Guid storeId)
    {
        Guid id = clsUtil.generateGuid();
        Banner categoryObj = new Banner
        {
            Id = id,
            EndAt = endAt,
            CreatedAt = DateTime.Today,
            Image = image,
            StoreId = storeId
        };
        await _unitOfWork.BannelRepository.addAsync(categoryObj);
        int result = await _unitOfWork.Complate();

        if (result == 0) return null;
        
        return await getBanner(id);

    }

    
    public async Task<bool> deleteBanner(
        Guid id)
    {
        await _unitOfWork.BannelRepository.deleteAsync(id);
        int result = await _unitOfWork.Complate();
        return (result!=0);
    }


    public async Task<int?> getBannersCount()
    {
            return await _dbContext.Banner
                .Where(ban => ban.EndAt > DateTime.Now)
                .AsNoTracking()
                .CountAsync();
    }
    
    
}