using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared;
using ecommerc_dotnet.shared.extentions;
using ecommerc_dotnet.shared.signalr;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.application.services_implementaion;

public class OrderItemServices(
    IOrderItemRepository orderItemRepository,
    IUserRepository userRepository,
    IConfig config,
    IHubContext<OrderItemHub> hubContext)
    :IOrderItemServices 
{
   
    public async Task<Result<List<OrderItemDto>>> getOrderItmes(
        Guid userId, 
        int pageNum, 
        int pageSize)
    {
        User? user = await userRepository.getUser(userId);

        var isValidate = user.isValidateFunc(isAdmin:null,isStore:true);
        if (isValidate is not null)
        {
            return new Result<List<OrderItemDto>>(
                data: new List<OrderItemDto>(),
                message: isValidate.Message,
                isSeccessful: false,
                statusCode: isValidate.StatusCode
            );
        }
        
        List<OrderItemDto> orderItems = (await orderItemRepository
                .getOrderItems(storeId: user.Store.Id, pageNum: pageNum, pageSize: pageSize))
            .Select(p => p.toOrderItemDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderItemDto>>
        (
            data: orderItems,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<int>> updateOrderItmesStatus(
        Guid userId,
        UpdateOrderItemStatusDto orderItemsStatusDto)
    {
        OrderItem? orderItem = await orderItemRepository.getOrderItem(orderItemsStatusDto.Id);

        if (orderItem is null)
        {
            return new Result<int>
            (
                data: 0,
                message: "orderItem not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        ;
        int result = await orderItemRepository.updateOrderItemStatus(
            orderItemsStatusDto.Id
            ,
            orderItemsStatusDto.Status == enOrderItemStatusDto.Excepted
                ? enOrderItemStatus.Excepted
                : enOrderItemStatus.Cancelled
        );

        if (result == 0)
        {
            return new Result<int>
            (
                data: 0,
                message: "error while update orderItme status",
                isSeccessful: false,
                statusCode: 400
            );
        }

        OrderItemsStatusEvent statusEvent = new OrderItemsStatusEvent
        {
            OrderId = orderItem.OrderId,
            OrderItemId = orderItem.Id,
            Status = enOrderItemStatus.Excepted.ToString()
        };
        await hubContext.Clients.All.SendAsync("orderItemsStatusChange", statusEvent);

        return new Result<int>
        (
            data: 1,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

  
}