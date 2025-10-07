using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
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

namespace ecommerc_dotnet.infrastructure.services;

public class OrderServices(
    IDeliveryRepository deliveryRepository,
    IOrderRepository orderRepository,
    IUserRepository userRepository,
    IConfig config,
    IHubContext<OrderHub> hubContext)
    : IOrderServices
{
    private async Task<Result<bool>?> isValideDelivery(Guid id, bool isAdmin = false)
    {
        if (!isAdmin)
        {
            Delivery? delivery = await deliveryRepository.getDelivery(id);

            if (delivery is null)
                return new Result<bool>
                (
                    data: false,
                    message: "delivery not  found ",
                    isSeccessful: false,
                    statusCode: 404
                );
            if (delivery.IsBlocked)
            {
                return new Result<bool>
                (
                    data: false,
                    message: "delivery is blocked ",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }
        else
        {
            User? user = await userRepository
                .getUser(id);
            if (user is null)
            {
                return new Result<bool>
                (
                    data: false,
                    message: "user not found",
                    isSeccessful: false,
                    statusCode: 404
                );
            }

            if (user.Role != 0)
            {
                return new Result<bool>
                (
                    data: false,
                    message: "not authorized user",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }


        return null;
    }

    public async Task<Result<OrderDto?>> CreateOrder(Guid userId, CreateOrderDto orderDto)
    {
        User? user = await userRepository.getUser(userId);

        var isValidated = user.isValidateFunc(isAdmin: false); 

        if (isValidated is not null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: isValidated.Message,
                isSeccessful: false,
                statusCode:isValidated.StatusCode 
            );
        }

        if (!(await orderRepository.isValidTotalPrice(orderDto.TotalPrice, orderDto.Items)))
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "order totalPrice is not valid",
                isSeccessful: false,
                statusCode: 400
            );
        }

        var id = clsUtil.generateGuid();
        Order? order = new Order
        {
            Id = id,
            Longitude = orderDto.Longitude,
            Latitude = orderDto.Latitude,
            UserId = userId,
            TotalPrice = orderDto.TotalPrice,
            Status = 1,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
        };

        //order.Items = 
        List<OrderItem> orderItems = new List<OrderItem>();
        foreach (var item in orderDto.Items)
        {
            var orderItemId = clsUtil.generateGuid();
            List<OrderProductsVarient> orderProductsVarients = item.ProductsVarientId
                .Select(x => new OrderProductsVarient
                {
                    Id = clsUtil.generateGuid(),
                    OrderItemId = orderItemId,
                    ProductVarientId = x
                })
                .ToList();
            OrderItem orderItem = new OrderItem
            {
                Id = orderItemId,
                OrderId = id,
                ProductId = item.ProductId,
                Quanity = item.Quantity,
                StoreId = item.StoreId,
                Price = item.Price,
                OrderProductsVarients = orderProductsVarients
            };
            orderItems.Add(orderItem);
        }

        order.Items = orderItems;
        int result = await orderRepository.addAsync(order);

        if (result == 0)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSeccessful: false,
                statusCode: 400
            );
        }

        order = await orderRepository.getOrder(order.Id);
        if (order is null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSeccessful: false,
                statusCode: 400
            );
        }

        var dtoOrder = order.toDto(config.getKey("url_file"));
        await hubContext.Clients.All.SendAsync("createdOrder", dtoOrder);

        return new Result<OrderDto?>
        (
            data: dtoOrder,
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<List<OrderDto>>> getMyOrders(Guid userId, int pageNum, int pageSize)
    {
        List<OrderDto> orders = (await orderRepository
                .getOrders(userId, pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    //for admin dashboard
    public async Task<Result<List<OrderDto>>> getOrders(Guid userId, int pageNum, int pageSize)
    {
        Result<bool>? isValide = await isValideDelivery(userId, true);

        if (isValide is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValide.Message,
                isSeccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        List<OrderDto> orders = (await orderRepository
                .getAllAsync(pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> updateOrderStatus(Guid id, int status)
    {
        Order? order = await orderRepository
            .getOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        order.Status = status;
        int result = await orderRepository.updateAsync(order);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order status",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
  
    public async Task<Result<bool>> deleteOrder(Guid id, Guid userId)
    {
        Order? order = await orderRepository.getOrder(id, userId);
        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int result = await orderRepository.deleteAsync(id);
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete order",
                isSeccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }


    // for delivery 
    public async Task<Result<List<OrderDto>>> getOrdersbyDeliveryId(Guid deliveryId, int pageNum, int pageSize)
    {
        Result<bool>? isValide = await isValideDelivery(deliveryId);
        if (isValide is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValide.Message,
                isSeccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        List<OrderDto> orders = (await orderRepository
                .getOrderBelongToDelivery(deliveryId, pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<OrderDto>>> getOrdersNotBelongToDeliveries(Guid deliveryId, int pageNum, int pageSize)
    {
        Result<bool>? isValide = await isValideDelivery(deliveryId);
        if (isValide is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValide.Message,
                isSeccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        List<OrderDto> orders = (await orderRepository
                .getOrderNoBelongToAnyDelivery(pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> submitOrderToDelivery(Guid id, Guid deliveryId)
    {
        Result<bool>? isValide = await isValideDelivery(deliveryId);

        if (isValide is not null)
        {
            return isValide;
        }


        Order? order = await orderRepository.getOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (order.DeleveryId is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order already has delivered by  another delivery ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        order.DeleveryId = deliveryId;
        int result = await orderRepository.updateAsync(order);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order",
                isSeccessful: false,
                statusCode: 400
            );
        }

        OrderTakedByEvent eventHolder = new OrderTakedByEvent
        {
            Id = order.Id,
            DeliveryId = deliveryId
        };
        await hubContext.Clients.All.SendAsync("orderGettingByDelivery", eventHolder);

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> cancelOrderFromDelivery(Guid id, Guid deliveryId)
    {
        Result<bool>? isValide = await isValideDelivery(deliveryId);

        if (isValide is not null)
        {
            return isValide;
        }


        Order? order = await orderRepository.getOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (!(await orderRepository.isCanCancelOrder(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "order can not cancel some orderitems recived from stores by delivery ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int result = await orderRepository.removeOrderFromDelivery(id, deliveryId);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while remove order from delivery",
                isSeccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("createdOrder", order.toDto(config.getKey("url_file")));

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
}