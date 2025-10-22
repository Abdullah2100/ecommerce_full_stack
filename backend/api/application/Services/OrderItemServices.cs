using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.shared.extentions;
using ecommerc_dotnet.shared.signalr;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.application.Services;

public class OrderItemServices(
    IConfig config,
    IHubContext<OrderItemHub> hubContext,
    IUnitOfWork unitOfWork,
    IOrderServices orderServices
)
    : IOrderItemServices
{
    public async Task<Result<List<OrderItemDto>>> getOrderItmes(
        Guid storeId,
        int pageNum,
        int pageSize)
    {
        User? user = await unitOfWork.UserRepository.getUser(storeId);

        var isValidate = user.isValidateFunc(isAdmin: false, isStore: true);
        if (isValidate is not null)
        {
            return new Result<List<OrderItemDto>>(
                data: new List<OrderItemDto>(),
                message: isValidate.Message,
                isSuccessful: false,
                statusCode: isValidate.StatusCode
            );
        }

        List<OrderItemDto> orderItems = (await unitOfWork.OrderItemRepository
                .getOrderItems(storeId: user.Store.Id, pageNum: pageNum, pageSize: pageSize))
            .Select(p => p.toOrderItemDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderItemDto>>
        (
            data: orderItems,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<int>> updateOrderItmesStatus(
        Guid userId,
        UpdateOrderItemStatusDto orderItemsStatusDto)
    {
        OrderItem? orderItem = await unitOfWork.OrderItemRepository.getOrderItem(orderItemsStatusDto.Id);

        if (orderItem is null)
        {
            return new Result<int>
            (
                data: 0,
                message: "orderItem not found",
                isSuccessful: false,
                statusCode: 404
            );
        } ;
        
        orderItem.Status = orderItemsStatusDto.Status == enOrderItemStatusDto.Excepted
            ? enOrderItemStatus.Excepted
            : orderItemsStatusDto.Status == enOrderItemStatusDto.TakedByDelivery
                ? enOrderItemStatus.ReceivedByDelivery
                : enOrderItemStatus.Cancelled;
        
        unitOfWork.OrderItemRepository.update(orderItem);
        
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<int>
            (
                data: 0,
                message: "error while update orderItme status",
                isSuccessful: false,
                statusCode: 400
            );
        }

        OrderItemsStatusEvent statusEvent = new OrderItemsStatusEvent
        {
            OrderId = orderItem.OrderId,
            OrderItemId = orderItem.Id,
            Status = orderItem.Status.ToString()
        };
        await hubContext.Clients.All.SendAsync("orderItemsStatusChange", statusEvent);

       

        return new Result<int>
        (
            data: 1,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

 

}