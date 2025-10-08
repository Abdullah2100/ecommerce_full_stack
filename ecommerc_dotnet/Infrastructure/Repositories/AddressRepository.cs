using ecommerc_dotnet.application;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.domain.entity;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class AddressRepository(AppDbContext context) : IAddressRepository
{
    public void add(Address entity)
    {
        context.Address.Add(entity);
    }


    public void update(Address entity)
    {
        context.Address.Update(entity);
    }

    public void   deleteAsync(Guid id)

    {
       var address = context
            .Address
            .Where(x => x.Id == id);
       context.RemoveRange(address);
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

    public async Task<List<Address>> getAllAddressByOwnerId(Guid id)
    {
        return await context
            .Address
            .Where(x => x.OwnerId == id)
            .ToListAsync();
    }

    public void updateCurrentLocation(Guid id, Guid ownerId)
    {
        var currentAddress = context.Address
            .FirstOrDefault(ad => ad.OwnerId == ownerId && ad.Id == id);
        if (currentAddress == null) throw new ArgumentNullException();
        currentAddress.IsCurrent = true;
        
    }

    public  void makeAddressNotCurrentToId(Guid ownerId)
    {
        var address =  context.Address
            .Where(ad => ad.OwnerId == ownerId);
        foreach (var currentAddress in address)
        {
           currentAddress.IsCurrent = false; 
        }
        context.UpdateRange(address);
    }

    public void delete(Guid id)
    {
        var address= context
            .Address
            .FirstOrDefault(x => x.Id == id);
        if (address is null) throw new ArgumentNullException();
        context.Remove(address);
    }
}