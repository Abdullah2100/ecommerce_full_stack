using ecommerc_dotnet.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.shared.signalr;

public class StoreHub : Hub
{
    public async Task updatingStoreStatus(StoreStatusDto status)
    {
        await Clients.All.SendAsync("storeStatus", status);
    }
}