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

  
     
  
}