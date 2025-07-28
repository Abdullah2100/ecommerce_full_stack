using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.entity;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class GeneralData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;

    public GeneralData(
        AppDbContext dbContext,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _unitOfWork = unitOfWork;
    }


    public async Task<GeneralSetting?> getGeneralSettings(Guid id)
    {
        try
        {
            return await _dbContext.GeneralSettings
                .AsNoTracking()
                .FirstOrDefaultAsync(gs => gs.Id == id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<List<GeneralSettingDto>?> getGeneralSettingList(
        int pageNumber,
        int pageSize = 25)
    {
        return (await _unitOfWork.GeneralSettingsRepository
                .getAllAsync(pageNumber, pageSize))
            .Select(gs => gs.toDto())
            .ToList();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _dbContext.GeneralSettings.FindAsync(id) != null;
    }

    public async Task<bool> isExist(Guid id, string name)
    {
        return await _dbContext.GeneralSettings
            .AsNoTracking()
            .AnyAsync(gs => gs.Id == id && gs.Name == name);
    }

    public async Task<int> generalSettingCount()
    {
        return await _dbContext.GeneralSettings
            .AsNoTracking()
            .CountAsync();
    }


    public async Task<GeneralSetting?> createGeneralSetting(
        string name,
        decimal value)
    {
        Guid id = clsUtil.generateGuid();
        await _unitOfWork.GeneralSettingsRepository.addAsync(
            new GeneralSetting
            {
                CreatedAt = DateTime.Now,
                Id = id,
                Name = name,
                Value = value
            }
        );
        int result = await _unitOfWork.Complate();
        if (result == 0) return null;
        return await getGeneralSettings(id);
    }

    public async Task<GeneralSetting?> updateGeneralSetting(
        Guid id,
        string name,
        decimal value
    )
    {
        await _dbContext
            .GeneralSettings
            .Where(gs => gs.Id == id)
            .ExecuteUpdateAsync(gs => gs
                .SetProperty(generalSetting => generalSetting.Name, name)
                .SetProperty(generalSetting => generalSetting.UpdatedAt, DateTime.Now)
                .SetProperty(generalSetting => generalSetting.Value, value));

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return null;

        return await getGeneralSettings(id);
    }

    public async Task<bool> deleteGeneralSetting(Guid id)
    {
        await _unitOfWork.GeneralSettingsRepository.deleteAsync(id);
        int result = await _unitOfWork.Complate();
        if (result == 0) return false;
        return true;
    }
}