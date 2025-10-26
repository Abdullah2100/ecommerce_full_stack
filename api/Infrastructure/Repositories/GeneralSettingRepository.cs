using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class GeneralSettingRepository(AppDbContext context) : IGeneralSettingRepository
{
    public async Task<IEnumerable<GeneralSetting>> Getgenralsettings(int page, int length)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }
    public void Add(GeneralSetting entity)
    {
        context.GeneralSettings.Add(entity);
    }

    public void Update(GeneralSetting entity)
    {
        context.GeneralSettings.Update(entity);
    }

    public void Delete(Guid id)
    {
        var generalSetting = context.GeneralSettings
            .FirstOrDefault(gs => gs.Id == id);
        if (generalSetting == null) throw new ArgumentNullException();

        context.Remove(generalSetting);
    }

    public async Task<GeneralSetting?> GetGeneralSetting(Guid id)
    {
        return await context.GeneralSettings.FindAsync(id);
    }


    public async Task<bool> IsExist(Guid id)
    {
        return await context.GeneralSettings.FindAsync(id) != null;
    }

    public async Task<bool> IsExist(string name)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Name == name);
    }

    public async Task<bool> IsExist(Guid id, string name)
    {
        return await context
            .GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Id == id && gs.Name == name);
    }
}