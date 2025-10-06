using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.application.services;

public interface IDeliveryServices
{
    Task<Result<AuthDto?>> login(LoginDto loginDto);
    
    Task<Result<DeliveryDto?>> createDelivery(Guid userId,CreateDeliveryDto deliveryDto);
    Task<Result<DeliveryDto?>> updateDeliveryStatus(Guid id,bool status);
    
    Task<Result<DeliveryDto?>> getDelivery(Guid id);
    
    Task<Result<List<DeliveryDto>>> getDeliveries(Guid adminId, int pageNumber, int pageSize);
    Task<Result<DeliveryDto>> updateDelivery(UpdateDeliveryDto deliveryDto,Guid id);
    
}