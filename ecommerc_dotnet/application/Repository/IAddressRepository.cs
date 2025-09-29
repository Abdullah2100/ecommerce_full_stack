using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IAddressRepository : IRepository<Address>
{
    Task<int> getAddressCount(Guid id);
    Task<Address?> getAddress(Guid id);
    Task<Address?> getAddressByOwnerId(Guid id);
    Task<int> updateCurrentLocation(Guid id, Guid ownerId);
    Task<int> makeAddressNotCurrentToId(Guid ownerId);
}