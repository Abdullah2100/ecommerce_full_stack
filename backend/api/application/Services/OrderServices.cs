using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.shared;
using ecommerc_dotnet.shared.extentions;
using ecommerc_dotnet.shared.signalr;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.application.Services;

public class OrderServices(
    IUnitOfWork unitOfWork,
    IConfig config,
    IMessageSerivice messageSerivice,
    IHubContext<OrderHub> hubContext)
    : IOrderServices
{
    public static List<string> OrderStatus = new List<string>
    {
        "Rejected",
        "Inprogress",
        "Accepted",
        "In away",
        "Received",
        "Completed",
    };


    public async Task<Result<OrderDto?>> CreateOrder(Guid userId, CreateOrderDto orderDto)
    {
        User? user = await unitOfWork.UserRepository.getUser(userId);

        var isValidated = user.isValidateFunc(isAdmin: false);

        if (isValidated is not null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: isValidated.Message,
                isSuccessful: false,
                statusCode: isValidated.StatusCode
            );
        }

        if (!(await unitOfWork.OrderRepository.isValidTotalPrice(orderDto.TotalPrice, orderDto.Items)))
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "order totalPrice is not valid",
                isSuccessful: false,
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

        unitOfWork.OrderRepository.add(order);

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
            unitOfWork.OrderItemRepository.add(orderItem);
            unitOfWork.OrderProductVariantRepository.add(orderProductsVarients);
        }


        int result = await unitOfWork.saveChanges();


        if (result == 0)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var isSavedDistance = await unitOfWork.OrderRepository.isSavedDistanceToOrder(order.Id);

        if (isSavedDistance == false)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "could not calculate  distance distance to user ",
                isSuccessful: false,
                statusCode: 400
            );
        }

        order = await unitOfWork.OrderRepository.getOrder(order.Id);
        if (order is null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var dtoOrder = order.toDto(config.getKey("url_file"));
        await hubContext.Clients.All.SendAsync("createdOrder", dtoOrder);

        return new Result<OrderDto?>
        (
            data: dtoOrder,
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<List<OrderDto>>> getMyOrders(Guid userId, int pageNum, int pageSize)
    {
        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .getOrders(userId, pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    //for admin dashboard
    public async Task<Result<AdminOrderDto?>> getOrders(Guid userId, int pageNum, int pageSize)
    {
        User? delivery = await unitOfWork.UserRepository.getUser(userId);

        var isValid = delivery.isValidateFunc(true);

        if (isValid is not null)
        {
            return new Result<AdminOrderDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .getOrders(pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        int orderPages = (int)Math.Ceiling((double)orders.Count / pageSize);

        var holder = new AdminOrderDto { Orders = orders, pageNum = orderPages };
        return new Result<AdminOrderDto?>
        (
            data: holder,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> updateOrderStatus(Guid id, int status)
    {
        Order? order = await unitOfWork.OrderRepository
            .getOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        order.Status = status;

        unitOfWork.OrderRepository.update(order);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order status",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("orderStatus", new UpdateOrderStatusEventDto
        {
            Id = order.Id,
            Status = OrderStatus[status]
        });

        //this for notification operation for all user at the system

        await sendNotification(order, status);
        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> deleteOrder(Guid id, Guid userId)
    {
        Order? order = await unitOfWork.OrderRepository.getOrder(id, userId);
        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.OrderRepository.delete(id);
        int result = await unitOfWork.saveChanges();
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }


    // for delivery 
    public async Task<Result<List<OrderDto>>> getOrdersbyDeliveryId(Guid deliveryId, int pageNum, int pageSize)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.getDelivery(deliveryId);

        var isValid = delivery.isValidated();

        if (isValid is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .getOrderBelongToDelivery(deliveryId, pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<OrderDto>>> getOrdersNotBelongToDeliveries(Guid deliveryId, int pageNum, int pageSize)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.getDelivery(deliveryId);

        var isValid = delivery.isValidated();
        if (isValid is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .getOrderNoBelongToAnyDelivery(pageNum, pageSize))
            .Select(o => o.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<bool>> submitOrderToDelivery(Guid id, Guid deliveryId)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.getDelivery(deliveryId);

        var isValid = delivery.isValidated();

        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }


        Order? order = await unitOfWork.OrderRepository.getOrder(id);

        if (order == null)
        {
            return new Result<bool>
            (
                data: false,
                message: "Order not exists",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (order.DeleveryId != null)
            return new Result<bool>
            (
                data: false,
                message: "Order Delivered By another Delivery",
                isSuccessful: false,
                statusCode: 404
            );


        order.DeleveryId = deliveryId;
        order.UpdatedAt = DateTime.Now;
        
        unitOfWork.OrderRepository.update(order);

        var result = await unitOfWork.saveChanges();


        if (result <1)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        OrderTookByEvent eventHolder = new OrderTookByEvent
        {
            Id = id,
            DeliveryId = deliveryId
        };

        await hubContext.Clients.All.SendAsync("orderGettingByDelivery", eventHolder);
        await hubContext.Clients.All.SendAsync("orderStatus", new UpdateOrderStatusEventDto
        {
            Id = order.Id,
            Status = OrderStatus[2]
        });
        

        await sendNotification(order, status:2);
        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> cancelOrderFromDelivery(Guid id, Guid deliveryId)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.getDelivery(deliveryId);

        var isValid = delivery.isValidated();
        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        Order? order = await unitOfWork.OrderRepository.getOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (!(await unitOfWork.OrderRepository.isCanCancelOrder(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "order can not cancel some orderitems recived from stores by delivery ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.OrderRepository.removeOrderFromDelivery(id, deliveryId);
        int result = await unitOfWork.saveChanges();


        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while remove order from delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("createdOrder", order.toDto(config.getKey("url_file")));

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<string>>> getOrdersStatus(Guid adminId)
    {
        User? user = await unitOfWork.UserRepository.getUser(adminId);
        var isValide = user.isValidateFunc();

        if (isValide is not null)
        {
            return new Result<List<string>>(
                data: new List<string>(),
                message: isValide.Message,
                isSuccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        return new Result<List<string>>(
            data: OrderStatus,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    private async Task sendNotification(Order order, int status)
    {

        await sendNotificationToStore(order, status);
        await sendNotificationToUser(order, status);
        await sendNotificationToDelivery(order, status);
    }
    
    private async Task sendNotificationToStore(Order order, int status)
    {
        try
        {
            var messageServe = new SendMessageSerivcies(new NotificationServices());

            var orderItems = order.Items.ToList();

            for (int i = 0; i < orderItems.Count; i++)
            {
                var orderItem = orderItems[i];
                var cancelMessage = orderItem.Product.Name + " is Rejected For " + order.User.Name;
                var storeMessage = this.storeMessage(status, cancelMessage);
                if (!string.IsNullOrEmpty(storeMessage))
                {
                    await messageServe.sendMessage(storeMessage, orderItem.Store.user.deviceToken);
                }
            }
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private async Task sendNotificationToUser(Order order, int status){
        try
        {
            var messageServe = new SendMessageSerivcies(new NotificationServices());

            var userMessage = this.userMessage(status);
            if (!string.IsNullOrEmpty(userMessage))
            {
                await messageServe.sendMessage(userMessage, order.User.deviceToken);
            }
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private async Task sendNotificationToDelivery(Order order, int status)
    {
        try
        {
            var messageServe = new SendMessageSerivcies(new NotificationServices());

            var deliveryMessage = this.deliveryMessage(status);

            Delivery? delivery = null;
            if (order.DeleveryId is not null)
            {
                delivery = await unitOfWork.DeliveryRepository.getDelivery(order.DeleveryId ?? Guid.Empty);
            }

            switch (status)
            {
                case 1:
                {
                    await messageServe.sendMessage(deliveryMessage, delivery.DeviceToken);
                }
                    break;

                case 3:
                {
                    await messageServe.sendMessage(deliveryMessage, delivery.DeviceToken);
                }
                    break;
            }
        }
        catch
            (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private string userMessage(int status)
    {
        return status switch
        {
            0 => "Your Order is Rejected",
            2 => "Your Order Will Start Operation",
            3 => "Your Order in Away to Your Place",
            4 => "Your Order is Received",
            5 => "Your Order is Delivered",
            _ => ""
        };
    }

    private string deliveryMessage(int status)
    {
        return status switch
        {
            0 => "Order is Rejected",
            5 => "Your Order is Received",
            _ => ""
        };
    }

    private string storeMessage(int status, string customMessage = "")
    {
        return status switch
        {
            0 => customMessage,
            2 => "There Are New Order For Your Store Check them",
            5 => "Your Order is Delivered",
            _ => ""
        };
    }
}