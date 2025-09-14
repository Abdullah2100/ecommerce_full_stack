using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class GeneralSettingRepository(AppDbContext context) : IGeneralSettingRepository
{
    public async Task<IEnumerable<GeneralSetting>> getAllAsync(int page, int length)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(GeneralSetting entity)
    {
        await  context.GeneralSettings.AddAsync(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(GeneralSetting entity)
    {
        context.GeneralSettings.Update(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context.GeneralSettings
            .Where(gs => gs.Id == id)
            .ExecuteDeleteAsync();
        return await context.SaveChangesAsync();
    }

    public async Task<GeneralSetting?> getGeneralSetting(Guid id)
    {
        return  await context.GeneralSettings.FindAsync(id);
    }

 

    public async Task<bool> isExist(Guid id)
    {
        return  await context.GeneralSettings.FindAsync(id)!=null;
    }

    public async Task<bool> isExist(string name)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Name == name);
    }

    public async Task<bool> isExist(Guid id, string name)
    {
        return  await context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs=>gs.Id==id&&gs.Name==name);

    }
}