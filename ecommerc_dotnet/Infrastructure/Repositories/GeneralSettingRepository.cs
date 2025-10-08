using ecommerc_dotnet.application;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class GeneralSettingRepository(AppDbContext context) : IGeneralSettingRepository
{
    public async Task<IEnumerable<GeneralSetting>> getgenralsettings(int page, int length)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }
    public void add(GeneralSetting entity)
    {
        context.GeneralSettings.Add(entity);
    }

    public void update(GeneralSetting entity)
    {
        context.GeneralSettings.Update(entity);
    }

    public void delete(Guid id)
    {
        var generalSetting = context.GeneralSettings
            .FirstOrDefault(gs => gs.Id == id);
        if (generalSetting == null) throw new ArgumentNullException();

        context.Remove(generalSetting);
    }

    public async Task<GeneralSetting?> getGeneralSetting(Guid id)
    {
        return await context.GeneralSettings.FindAsync(id);
    }


    public async Task<bool> isExist(Guid id)
    {
        return await context.GeneralSettings.FindAsync(id) != null;
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
        return await context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Id == id && gs.Name == name);
    }
}