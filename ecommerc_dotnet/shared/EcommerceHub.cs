using ecommerc_dotnet.dto;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.services;

public class EcommerceHub : Hub
{

    public async Task sendingNewBanner(BannerDto banner)
    {
        await Clients.All.SendAsync("createdBanner", banner);
    }

    public async Task sendingNewOrder(OrderDto order)
    {
        await Clients.All.SendAsync("createdOrder", order);
    }


    public async Task orderGettingByCustomer(OrderTakedByEvent orderUpdatedEvent)
    {
        await Clients.All.SendAsync("orderGettingByDelivery", orderUpdatedEvent);
    }



    public async Task updatingStoreStatus(StoreStatusDto status)
    {
        await Clients.All.SendAsync("storeStatus", status);
    }


    public async Task excptedOrderByAdmin(OrderDto order)
    {
        await Clients.All.SendAsync("orderExcptedByAdmin", order);
    }
    public async Task sendingOrderItemStatusChange(OrderItemsStatusEvent orderItemsStatus)
    {
        await Clients.All.SendAsync("orderItemsStatusChange", orderItemsStatus);
        
    }
}