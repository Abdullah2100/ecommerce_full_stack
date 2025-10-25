using api.domain.entity;
using api.domain.Interface;
using ecommerc_dotnet.application;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class AddressRepository(AppDbContext context) : IAddressRepository
{
    public void Add(Address entity)
    {
        context.Address.Add(entity);
    }


    public void Update(Address entity)
    {
        context.Address.Update(entity);
    }


    public Task<int> GetAddressCount(Guid id)
    {
        return context
            .Address
            .AsNoTracking().Where(ad => ad.OwnerId == id)
            .CountAsync();
    }

    public async Task<Address?> GetAddress(Guid id)
    {
        return await context.Address.FindAsync(id);
    }

    public async Task<Address?> GetAddressByOwnerId(Guid id)
    {
        return await context
            .Address
            .FirstOrDefaultAsync(x => x.OwnerId == id);
    }

    public async Task<List<Address>> GetAllAddressByOwnerId(Guid id)
    {
        return await context
            .Address
            .Where(x => x.OwnerId == id)
            .ToListAsync();
    }

    public void UpdateCurrentLocation(Guid id, Guid ownerId)
    {
        var currentAddress = context.Address
            .FirstOrDefault(ad => ad.OwnerId == ownerId && ad.Id == id);
        if (currentAddress == null) throw new ArgumentNullException();
        currentAddress.IsCurrent = true;
        
    }

    public  void MakeAddressNotCurrentToId(Guid ownerId)
    {
        var address =  context.Address
            .Where(ad => ad.OwnerId == ownerId);
        foreach (var currentAddress in address)
        {
           currentAddress.IsCurrent = false; 
        }
        context.UpdateRange(address);
    }

    public void Delete(Guid id)
    {
        var address= context
            .Address
            .FirstOrDefault(x => x.Id == id);
        if (address is null) throw new ArgumentNullException();
        context.Remove(address);
    }
}