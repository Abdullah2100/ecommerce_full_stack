using api.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace api.shared.signalr;

public class BannerHub : Hub
{
    public async Task SendingNewBanner(BannerDto banner)
    {
        await Clients.All.SendAsync("createdBanner", banner);
    }
    
    public async Task SendDeletedBannerEvent(Guid bannerId)
    {
        await Clients.All.SendAsync("deletedOrder", bannerId);
    }
}