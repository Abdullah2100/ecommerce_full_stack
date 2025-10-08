using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.application.Repository;

public interface IOrderItemRepository: IRepository<OrderItem>
{
 
    
    Task<IEnumerable<OrderItem>> getOrderItems(Guid storeId,int pageNum,int pageSize);
    Task<OrderItem?> getOrderItem(Guid id ,Guid storeId);
    Task<OrderItem?> getOrderItem(Guid id );
    void  updateOrderItemStatus(Guid id,enOrderItemStatus status );
}