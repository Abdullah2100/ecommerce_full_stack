using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class GeneralSettingRepository:IGeneralSettingRepository
{
    private readonly AppDbContext _context;

    public GeneralSettingRepository(AppDbContext context)
    {
        _context = context;
    }
    public async Task<IEnumerable<GeneralSetting>> getAllAsync(int page, int length)
    {
        return await _context
            .GeneralSettings
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(GeneralSetting entity)
    {
        await  _context.GeneralSettings.AddAsync(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(GeneralSetting entity)
    {
        _context.GeneralSettings.Update(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context.GeneralSettings
            .Where(gs => gs.Id == id)
            .ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public async Task<GeneralSetting?> getGeneralSetting(Guid id)
    {
        return  await _context.GeneralSettings.FindAsync(id);
    }

 

    public async Task<bool> isExist(Guid id)
    {
        return  await _context.GeneralSettings.FindAsync(id)!=null;
    }

    public async Task<bool> isExist(string name)
    {
        return await _context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Name == name);
    }

    public async Task<bool> isExist(Guid id, string name)
    {
        return  await _context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs=>gs.Id==id&&gs.Name==name);

    }
}