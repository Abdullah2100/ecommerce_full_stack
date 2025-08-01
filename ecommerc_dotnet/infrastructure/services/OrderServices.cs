using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using ecommerc_dotnet.services;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.infrastructure.services;

public class OrderServices : IOrderServices
{
    private readonly IOrderRepository _orderRepository;
    private readonly IDeliveryRepository _deliveryRepository;
    private readonly IUserRepository _userRepository;
    private readonly IHubContext<EcommerceHub> _hubContext;

    public OrderServices(
        IDeliveryRepository deliveryRepository,
        IOrderRepository orderRepository,
        IUserRepository userRepository,
        IHubContext<EcommerceHub> hubContext
    )
    {
        _deliveryRepository = deliveryRepository;
        _orderRepository = orderRepository;
        _userRepository = userRepository;
        _hubContext = hubContext;
    }

    private async Task<Result<bool>?> isValideDelivery(Guid id, bool isAdmin = false)
    {
        if (!isAdmin)
        {
            Delivery? delivery = await _deliveryRepository.getDelivery(id);

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
            User? user = await _userRepository
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
        User? user = await _userRepository.getUser(userId);

        if (user is null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "user is blocked from creating order",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (!(await _orderRepository.isValidTotalPrice(orderDto.TotalPrice, orderDto.Items)))
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
        Order order = new Order
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
        int result = await _orderRepository.addAsync(order);

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

        await _hubContext.Clients.All.SendAsync("createdOrder", order.toDto());

        return new Result<OrderDto?>
        (
            data: order.toDto(),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<List<OrderDto>>> getMyOrders(Guid userId, int pageNum, int pageSize)
    {
        List<OrderDto> orders = (await _orderRepository
                .getOrders(userId, pageNum, pageSize))
            .Select(o => o.toDto())
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

        List<OrderDto> orders = (await _orderRepository
                .getAllAsync(pageNum, pageSize))
            .Select(o => o.toDto())
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
        Order? order = await _orderRepository
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
        int result = await _orderRepository.updateAsync(order);
        
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

    public async Task<Result<List<OrderItemDto>>> getOrderItmes(Guid storeId, int pageNum, int pageSize)
    {
        List<OrderItemDto> orderItems = (await _orderRepository
                .getOrderItems(storeId: storeId, pageNum: pageNum, pageSize: pageSize))
            .Select(p => p.toOrderItemDto())
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
        OrderItem? orderItem = await _orderRepository.getOrderItem(orderItemsStatusDto.Id);

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
        int result = await _orderRepository.updateOrderItemStatus(
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
        await _hubContext.Clients.All.SendAsync("orderItemsStatusChange", statusEvent);

        return new Result<int>
        (
            data: 1,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> deleteOrder(Guid id, Guid userId)
    {
        Order? order = await _orderRepository.getOrder(id, userId);
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

        int result = await _orderRepository.deleteAsync(id);
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

        List<OrderDto> orders = (await _orderRepository
                .getOrderBelongToDelivery(deliveryId, pageNum, pageSize))
            .Select(o => o.toDto())
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

        List<OrderDto> orders = (await _orderRepository
                .getOrderNoBelongToAnyDelivery(pageNum, pageSize))
            .Select(o => o.toDto())
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


        Order? order = await _orderRepository.getOrder(id);

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
        int result = await _orderRepository.updateAsync(order);

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
        await _hubContext.Clients.All.SendAsync("orderGettingByDelivery", eventHolder);

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


        Order? order = await _orderRepository.getOrder(id);

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

        if (!(await _orderRepository.isCanCancelOrder(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "order can not cancel some orderitems recived from stores by delivery ",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int result = await _orderRepository.removeOrderFromDelivery(id, deliveryId);

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

        await _hubContext.Clients.All.SendAsync("createdOrder", order.toDto());

        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
}