using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.application.services;

public interface IStoreServices
{
   public Task<Result<StoreDto?>> createStore(CreateStoreDto store,Guid userId);
   public Task<Result<StoreDto?>> updateStore(UpdateStoreDto storeDto,Guid userId);
   
   public Task<Result<StoreDto?>> getStoreByUserId(Guid userId);
   public Task<Result<StoreDto?>> getStoreByStoreId(Guid id);
   
   public Task<Result<List<StoreDto>?>> getStores(Guid adminId, int pageNumber, int pageSize);
   
   
   
   public Task<Result<bool?>> updateStoreStatus(Guid adminId,Guid storeId);
}