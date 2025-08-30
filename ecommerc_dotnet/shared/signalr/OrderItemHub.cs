using ecommerc_dotnet.dto;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.shared.signalr;

public class OrderItemHub : Hub
{
    public async Task sendingOrderItemStatusChange(OrderItemsStatusEvent orderItemsStatus)
    {
        await Clients.All.SendAsync("orderItemsStatusChange", orderItemsStatus);
    }
}