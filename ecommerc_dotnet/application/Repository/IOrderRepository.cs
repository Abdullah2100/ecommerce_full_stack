using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IOrderRepository:IRepository<Order>
{
    Task<IEnumerable<Order>> getOrders(Guid userId,int pageNum,int pageSize);
    
    Task<Order?> getOrder(Guid id);
    Task<Order?> getOrder(Guid id,Guid  userId);
    
    Task<bool> isExist(Guid id);
    Task<bool> isCanCancelOrder(Guid id);
    Task<bool> isValidTotalPrice(decimal totalPrice,List<CreateOrderItemDto> items);
    //delivery
    Task<IEnumerable<Order>> getOrderNoBelongToAnyDelivery(int pageNum,int pageSize);
    Task<IEnumerable<Order>> getOrderBelongToDelivery(Guid deliveryId,int pageNum,int pageSize);
    Task<int> removeOrderFromDelivery(Guid id,Guid deliveryId);
    
    
    //order items
    Task<IEnumerable<OrderItem>> getOrderItems(Guid storeId,int pageNum,int pageSize);
    Task<OrderItem?> getOrderItem(Guid id ,Guid storeId);
    Task<OrderItem?> getOrderItem(Guid id );
    Task<int> updateOrderItemStatus(Guid id,enOrderItemStatus status );
    
}