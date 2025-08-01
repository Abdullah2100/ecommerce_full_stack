using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.services;

public interface IOrderServices
{
    Task<Result<OrderDto?>> CreateOrder(Guid userId,CreateOrderDto orderDto);
    Task<Result<List<OrderDto>>> getMyOrders(Guid userId,int pageNum,int pageSize);
    
    //order for admin
    Task<Result<List<OrderDto>>> getOrders(Guid userId,int pageNum,int pageSize);

    Task<Result<bool>> updateOrderStatus(Guid id, int status);
    
    Task<Result<List<OrderItemDto>>> getOrderItmes(Guid storeId, int pageNum, int pageSize);
    
    Task<Result<int>> updateOrderItmesStatus(Guid userId, UpdateOrderItemStatusDto orderItemsStatusDto );
   Task<Result<bool>> deleteOrder(Guid id,Guid userId);
    
   //delivery 
   Task<Result<List<OrderDto>>> getOrdersbyDeliveryId(Guid deliveryId,int pageNum,int pageSize);
   Task<Result<List<OrderDto>>> getOrdersNotBelongToDeliveries(Guid deliveryId,int pageNum,int pageSize);
   Task<Result<bool>> submitOrderToDelivery(Guid id,Guid deliveryId);
   Task<Result<bool>> cancelOrderFromDelivery(Guid id,Guid deliveryId);


}