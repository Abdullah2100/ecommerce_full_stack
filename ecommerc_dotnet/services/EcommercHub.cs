using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
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

    public async Task sendingNewOrder(OrderResponseDto order)
    {
        await Clients.All.SendAsync("createdOrder", order);
    }


    public async Task orderGettingByCustomer(OrderUpdatedEvent orderUpdatedEvent)
    {
        await Clients.All.SendAsync("orderGettingByDelivery", orderUpdatedEvent);
    }



    public async Task updatingStoreStatus(StoreStatusResponseDto status)
    {
        await Clients.All.SendAsync("storeStatus", status);
    }


    public async Task excptedOrderByAdmin(OrderResponseDto order)
    {
        await Clients.All.SendAsync("orderExcptedByAdmin", order);
    }
    public async Task sendingOrderItemStatusChange(OrderItemsStatus orderItemsStatus)
    {
        await Clients.All.SendAsync("orderItemsStatusChange", orderItemsStatus);
        
    }
}