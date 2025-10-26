using api.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace api.shared.signalr;

public class OrderItemHub : Hub
{
    public async Task SendingOrderItemStatusChange(OrderItemsStatusEvent orderItemsStatus)
    {
        await Clients.All.SendAsync("orderItemsStatusChange", orderItemsStatus);
    }
}