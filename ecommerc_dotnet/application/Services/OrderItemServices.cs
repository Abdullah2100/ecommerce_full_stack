using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
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
    IUnitOfWork unitOfWork
)
    : IOrderItemServices
{
    public async Task<Result<List<OrderItemDto>>> getOrderItmes(
        Guid userId,
        int pageNum,
        int pageSize)
    {
        User? user = await unitOfWork.UserRepository.getUser(userId);

        var isValidate = user.isValidateFunc(isAdmin: false, isStore: true);
        if (isValidate is not null)
        {
            return new Result<List<OrderItemDto>>(
                data: new List<OrderItemDto>(),
                message: isValidate.Message,
                isSeccessful: false,
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
            isSeccessful: true,
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
                isSeccessful: false,
                statusCode: 404
            );
        }

        ;
        unitOfWork.OrderItemRepository.updateOrderItemStatus(
            orderItemsStatusDto.Id
            ,
            orderItemsStatusDto.Status == enOrderItemStatusDto.Excepted
                ? enOrderItemStatus.Excepted
                : enOrderItemStatus.Cancelled
        );
        int result = await unitOfWork.saveChanges();

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