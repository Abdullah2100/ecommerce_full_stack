using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class AddressRepository(AppDbContext context) : IAddressRepository
{
    public async Task<IEnumerable<Address>> getAllAsync(int page, int length)
    {
        return await context.Address
            .AsNoTracking()
            .Skip((page - 1) * length).Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Address entity)
    {
        context.Address.Add(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Address entity)
    {
        context.Address.Update(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context
            .Address
            .Where(x => x.Id == id)
            .ExecuteDeleteAsync();
        return await context.SaveChangesAsync();
    }

    public Task<int> getAddressCount(Guid id)
    {
        return context
            .Address
            .AsNoTracking().Where(ad => ad.OwnerId == id)
            .CountAsync();
    }

    public async Task<Address?> getAddress(Guid id)
    {
        return await context.Address.FindAsync(id);
    }

    public async Task<Address?> getAddressByOwnerId(Guid id)
    {
        return await context
            .Address
            .FirstOrDefaultAsync(x => x.OwnerId == id);
    }
    
    public async Task<int> updateCurrentLocation(Guid id, Guid ownerId)
    {
        await context.Address
            .Where(ad => ad.OwnerId == ownerId && ad.Id == id)
            .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, true));
        return 1;
    }

    public async Task<int> makeAddressNotCurrentToId(Guid ownerId)
    {
        await context.Address
            .Where(ad => ad.OwnerId == ownerId)
            .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, false));
        return 1;
    }
}