using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IDeliveryRepository:IRepository<Delivery>
{
    
    Task<Delivery?> getDelivery(Guid id);
    Task<Delivery?> getDeliveryByUserId(Guid userId);
    Task<List<Delivery>?> getDeliveryByBelongTo(Guid belongToId,int page, int size);
    
    Task<DeliveryAnalysDto> getDeliveryAnalys(Guid id);
    
    Task<bool> isExistByUserId(Guid userId);
    
}