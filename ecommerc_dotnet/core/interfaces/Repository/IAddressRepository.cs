using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IAddressRepository:IRepository<Address>
{
    Task<int> getAddressCount(Guid id);
    Task<Address?> getAddress(Guid id);
    Task<int> updateCurrentLocation(Guid id,Guid ownerId);
 
}