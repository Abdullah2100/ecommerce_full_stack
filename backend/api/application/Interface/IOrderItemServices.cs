using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IOrderItemServices
{
    
    Task<Result<List<OrderItemDto>>> GetOrderItmes(Guid storeId, int pageNum, int pageSize);
    
    Task<Result<int>> UpdateOrderItmesStatus(Guid userId, UpdateOrderItemStatusDto orderItemsStatusDto );
    

}