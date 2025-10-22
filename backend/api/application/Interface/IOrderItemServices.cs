using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface IOrderItemServices
{
    
    Task<Result<List<OrderItemDto>>> getOrderItmes(Guid storeId, int pageNum, int pageSize);
    
    Task<Result<int>> updateOrderItmesStatus(Guid userId, UpdateOrderItemStatusDto orderItemsStatusDto );
    

}