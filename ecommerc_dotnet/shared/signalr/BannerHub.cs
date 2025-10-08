using ecommerc_dotnet.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.shared.signalr;

public class BannerHub : Hub
{
    public async Task sendingNewBanner(BannerDto banner)
    {
        await Clients.All.SendAsync("createdBanner", banner);
    }
    
    public async Task sendDeletedBannerEvent(Guid bannerId)
    {
        await Clients.All.SendAsync("deletedOrder", bannerId);
    }
}