using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.domain.Interface;

public interface IOrderRepository:IRepository<Order>
{
    Task<IEnumerable<Order>> getOrders(Guid userId,int pageNum,int pageSize);
    Task<IEnumerable<Order>> getOrders(int page,int lenght);
    
    Task<Order?> getOrder(Guid id);
    Task<Order?> getOrder(Guid id,Guid  userId);
    
    Task<bool> isExist(Guid id);
    Task<bool> isCanCancelOrder(Guid id);
    Task<bool> isValidTotalPrice(decimal totalPrice,List<CreateOrderItemDto> items);
    //delivery
    Task<IEnumerable<Order>> getOrderNoBelongToAnyDelivery(int pageNum,int pageSize);
    Task<IEnumerable<Order>> getOrderBelongToDelivery(Guid deliveryId,int pageNum,int pageSize);
    void  removeOrderFromDelivery(Guid id,Guid deliveryId);
    Task<bool> isSavedDistanceToOrder(Guid id);
    void delete(Guid id);

}