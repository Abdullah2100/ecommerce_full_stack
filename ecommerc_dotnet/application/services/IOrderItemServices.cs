using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.application.services;

public interface IOrderItemServices
{
    
    Task<Result<List<OrderItemDto>>> getOrderItmes(Guid userId, int pageNum, int pageSize);
    
    Task<Result<int>> updateOrderItmesStatus(Guid userId, UpdateOrderItemStatusDto orderItemsStatusDto );
    

}