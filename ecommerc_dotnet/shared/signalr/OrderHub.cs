using ecommerc_dotnet.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.shared.signalr;

public class OrderHub : Hub
{
    public async Task sendingNewOrder(OrderDto order)
    {
        await Clients.All.SendAsync("createdOrder", order);
    }


    public async Task orderGettingByCustomer(OrderTakedByEvent orderUpdatedEvent)
    {
        await Clients.All.SendAsync("orderGettingByDelivery", orderUpdatedEvent);
    }


    public async Task updateOrderStatus(UpdateOrderStatusDto status)
    {
        await Clients.All.SendAsync("orderStatus", status);
    }


    public async Task expectedOrderByAdmin(OrderDto order)
    {
        await Clients.All.SendAsync("orderExcptedByAdmin", order);
    }
}