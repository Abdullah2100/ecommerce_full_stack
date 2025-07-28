using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class AddressRepository:IAddressRepository
{
   
    private readonly AppDbContext _dbContext;

    public AddressRepository(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<IEnumerable<Address>> getAllAsync(int page, int length)
    {
        return await _dbContext.Address
            .AsNoTracking()
            .Skip((page - 1) * length).Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(Address entity)
    {
        _dbContext.Address.Add(entity);
        return await _dbContext.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Address entity)
    {
        _dbContext.Address.Update(entity);
        return await _dbContext.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        Address? entity =await _dbContext
            .Address
            .FirstOrDefaultAsync(x => x.Id == id);
        if(entity is null) return 0;
        _dbContext.Address.Remove(entity);
        return await _dbContext.SaveChangesAsync();
    }

    public Task<int> getAddressCount(Guid id)
    {
        return _dbContext
            .Address
            .AsNoTracking().Where(ad => ad.OwnerId == id)
            .CountAsync();
    }

    public async Task<Address?> getAddress(Guid id)
    {
        return await _dbContext.Address.FindAsync(id);
    }

    public async Task<int> updateCurrentLocation(Guid id, Guid ownerId)
    {
      await  _dbContext.Address
          .Where(ad=>ad.OwnerId==ownerId&&ad.Id !=id)
            .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, false));
     
      await  _dbContext.Address
          .Where(ad=>ad.OwnerId==ownerId&&ad.Id ==id)
          .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, true));
      return await _dbContext.SaveChangesAsync();


    }
}