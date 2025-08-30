using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.application.Repository;

public interface IOrderItemRepository
{
 
    
    Task<IEnumerable<OrderItem>> getOrderItems(Guid storeId,int pageNum,int pageSize);
    Task<OrderItem?> getOrderItem(Guid id ,Guid storeId);
    Task<OrderItem?> getOrderItem(Guid id );
    Task<int> updateOrderItemStatus(Guid id,enOrderItemStatus status );
}