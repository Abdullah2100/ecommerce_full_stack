using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;

namespace hotel_api.Services;

public class EcommercHub : Hub
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;

    public EcommercHub(AppDbContext appDbContext,
        IConfig config)
    {
        _dbContext = appDbContext;
        _config = config;
    }
    public async Task sendingNewBanner(BannerResponseDto banner)
    {
        await Clients.All.SendAsync("createdBanner", banner);
    }

     public async Task streamBanners()
    {
        var bannerLenth = await getBanner();
        for (int i = 0; i < bannerLenth; i++)
        {
            var bannerList = await getBanners( 15);
            await Clients.All.SendAsync("Banners", bannerList);
        }
        await Clients.All.SendAsync("done");
    }
     
          public async Task<int?> getBanner()
    {
        try
        {
            return await _dbContext.Banner
                .AsNoTracking()
                .Where(ban=>ban.end_at>DateTime.Now)
                .CountAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }

    
    
    public async Task<List<BannerResponseDto>?> getBanners(int pageSize)
    {
        try
        {
            return await (from st in _dbContext.Store
                    join ba in _dbContext.Banner
                        on st.id equals ba.store_id
                    where  ba.end_at>DateTime.Now
                    select new BannerResponseDto
                    {
                        id = ba.id,
                        create_at = ba.create_at,
                        end_at = ba.end_at,
                        image = _config.getKey("url_file") + ba.image,
                        store_id = ba.store_id
                    }
                )
                .AsNoTracking()
                .OrderByDescending(ba=>Guid.NewGuid())
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