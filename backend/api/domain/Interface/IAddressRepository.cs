using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.domain.Interface;
public interface IAddressRepository : IRepository<Address>
{
    Task<int> getAddressCount(Guid id);
    Task<Address?> getAddress(Guid id);
    Task<Address?> getAddressByOwnerId(Guid id);
    Task<List<Address>> getAllAddressByOwnerId(Guid id);
    void updateCurrentLocation(Guid id, Guid ownerId);
    void makeAddressNotCurrentToId(Guid ownerId);
    void delete(Guid id);
}