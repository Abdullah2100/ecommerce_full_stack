using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class GeneralData
{
    private readonly AppDbContext _dbContext;

    public GeneralData(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

   
    public async Task<GeneralSettings?> getGeneralSettings(Guid id)
    {
        try
        {
            return await _dbContext.GeneralSettings
                .AsNoTracking()
                .FirstOrDefaultAsync(gs=>gs.id==id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    } 
    public async Task<List<GeneralSettingResponseDto>?> getGeneralSettingList(
        int pageNumber,
        int pageSize=25)
    {
        try
        {
            return await _dbContext.GeneralSettings
                .AsNoTracking()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(gs=> new GeneralSettingResponseDto{id=gs.id,name = gs.name,value = gs.value})
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<bool?> isExist(Guid id)
    {
        try
        {
            var result = await _dbContext.GeneralSettings.FindAsync(id);
            return result != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<bool?> isExist(Guid id, string name)
    {
        try
        {
            var result = await _dbContext.GeneralSettings
                .FirstOrDefaultAsync(gs => gs.id == id && gs.name == name);
            return result != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<int?> generalSettingCount()
    {
        try
        {
            return await _dbContext.GeneralSettings.CountAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }


    public async Task<GeneralSettings?> createGeneralSetting(
        string name,
        decimal value)
    {
        try
        {
            Guid id = clsUtil.generateGuid();
            var result = await _dbContext
                .GeneralSettings.AddAsync(
                    new GeneralSettings 
                    {
                        createdAt = DateTime.Now,
                        id = id,
                        name = name,
                        value = value
                    }
                );
            await _dbContext.SaveChangesAsync();
            return await getGeneralSettings(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from adding new sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<GeneralSettings?> updateGeneralSetting(
        Guid id,
        string name,
        decimal value
        )
    {
        try
        {
            GeneralSettings? result = await _dbContext
                .GeneralSettings.FindAsync(id);
            if (result == null) return null;

            result.updatedAt = DateTime.Now;
            result.name = name;
            result.value = value;
            
            await _dbContext.SaveChangesAsync();
            return await getGeneralSettings(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from update sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<bool?> deleteGeneralSetting(Guid id)
    {
        try
        {
            GeneralSettings? result = await _dbContext
                .GeneralSettings.FindAsync(id);
            if (result == null) return null;
            _dbContext.Remove(result);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from update sub category by id " + ex.Message);
            return null;
        }
    }
}