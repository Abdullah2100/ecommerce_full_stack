using api.domain.entity;

namespace api.domain.Interface;
public interface IAddressRepository : IRepository<Address>
{
    Task<int> GetAddressCount(Guid id);
    Task<Address?> GetAddress(Guid id);
    Task<Address?> GetAddressByOwnerId(Guid id);
    Task<List<Address>> GetAllAddressByOwnerId(Guid id);
    void UpdateCurrentLocation(Guid id, Guid ownerId);
    void MakeAddressNotCurrentToId(Guid ownerId);
    void Delete(Guid id);
}