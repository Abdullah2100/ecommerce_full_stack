using api.Presentation.dto;
using Microsoft.AspNetCore.SignalR;

namespace api.shared.signalr;

public class OrderHub : Hub
{
    public async Task SendingNewOrder(OrderDto order)
    {
        await Clients.All.SendAsync("createdOrder", order);
    }


    public async Task OrderGettingByCustomer(OrderTookByEvent orderUpdatedEvent)
    {
        await Clients.All.SendAsync("orderGettingByDelivery", orderUpdatedEvent);
    }


    public async Task UpdateOrderStatus(UpdateOrderStatusDto status)
    {
        await Clients.All.SendAsync("orderStatus", status);
    }


    public async Task ExpectedOrderByAdmin(OrderDto order)
    {
        await Clients.All.SendAsync("orderExcptedByAdmin", order);
    }
}