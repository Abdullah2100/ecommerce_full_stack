using api.domain.entity;
using api.Presentation.dto;

namespace api.domain.Interface;

public interface IDeliveryRepository:IRepository<Delivery>
{
    
    Task<Delivery?> GetDelivery(Guid id);
    Task<Delivery?> GetDeliveryByUserId(Guid userId);
    Task<List<Delivery>?> GetDeliveriesByBelongTo(Guid belongToId,int page, int size);
    Task<List<Delivery>?> GetDeliveries(int page, int size);
    Task<int> GetDeliveriesPage(int deliveryPerSize);
    
    
    Task<DeliveryAnalysDto> GetDeliveryAnalys(Guid id);
    
    Task<bool> IsExistByUserId(Guid userId);

    void Delete(Guid id);

}