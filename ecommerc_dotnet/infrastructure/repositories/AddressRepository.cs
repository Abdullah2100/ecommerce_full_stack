using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class AddressRepository:IAddressRepository
{
   
    private readonly AppDbContext _context;

    public AddressRepository(AppDbContext context)
    {
        _context = context;
    }
    
    public async Task<IEnumerable<Address>> getAllAsync(int page, int length)
    {
        return await _context.Address
            .AsNoTracking()
            .Skip((page - 1) * length).Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Address entity)
    {
        _context.Address.Add(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Address entity)
    {
        _context.Address.Update(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context
            .Address
            .Where(x => x.Id == id)
            .ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public Task<int> getAddressCount(Guid id)
    {
        return _context
            .Address
            .AsNoTracking().Where(ad => ad.OwnerId == id)
            .CountAsync();
    }

    public async Task<Address?> getAddress(Guid id)
    {
        return await _context.Address.FindAsync(id);
    }

    public async Task<Address?> getAddressByOwnerId(Guid id)
    {
        return await _context
            .Address
            .FirstOrDefaultAsync(x => x.OwnerId == id);
    }

    public async Task<int> updateCurrentLocation(Guid id, Guid ownerId)
    {
      await  _context.Address
          .Where(ad=>ad.OwnerId==ownerId&&ad.Id !=id)
            .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, false));
     
      await  _context.Address
          .Where(ad=>ad.OwnerId==ownerId&&ad.Id ==id)
          .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, true));
      return 1;


    }
}