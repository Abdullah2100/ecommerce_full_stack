using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.domain.Interface;

public interface IDeliveryRepository:IRepository<Delivery>
{
    
    Task<Delivery?> getDelivery(Guid id);
    Task<Delivery?> getDeliveryByUserId(Guid userId);
    Task<List<Delivery>?> getDeliveriesByBelongTo(Guid belongToId,int page, int size);
    Task<List<Delivery>?> getDeliveries();
    
    
    Task<DeliveryAnalysDto> getDeliveryAnalys(Guid id);
    
    Task<bool> isExistByUserId(Guid userId);

    void delete(Guid id);

}