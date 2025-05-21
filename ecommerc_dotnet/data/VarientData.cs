using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class VarientData
{
    private readonly AppDbContext _dbContext;

    public VarientData(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<VarientResposeDto?> getVarient(Guid id)
    {
        try
        {
            var result = await _dbContext.Varients.FindAsync(id);
            if (result == null) return null;
            return new VarientResposeDto
            {
                id = result.id,
                name = result.name,
            };
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return null;
        }
    }

    public async Task<bool> isExist(string name)
    {
        try
        {
            var result = await _dbContext.Varients.FirstOrDefaultAsync(va => va.name == name);
            return result != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return false;
        }
    }

    public async Task<bool> isExist(Guid id)
    {
        try
        {
            var result = await _dbContext.Varients
                .FirstOrDefaultAsync(va => va.id == id);
            return result != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return false;
        }
    }


    public async Task<VarientResposeDto?> createVarient(string name)
    {
        try
        {
            var id = clsUtil.generateGuid();
            await _dbContext.Varients.AddAsync(new Varient { id = id, name = name });
            await _dbContext.SaveChangesAsync();
            return await getVarient(id);
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new Varient " + ex.Message);
            return null;
        }
    }

    public async Task<VarientResposeDto?> updateVarient(string name, Guid id)
    {
        try
        {
            var result = await _dbContext.Varients.FindAsync(id);
            if (result == null) return null;
            result.name = name;

            await _dbContext.SaveChangesAsync();
            return await getVarient(id);
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new Varient " + ex.Message);
            return null;
        }
    }

    public async Task<bool> deleteVarient(
        Guid varrient_id)
    {
        try
        {
            var result = await _dbContext.Varients.FindAsync(varrient_id);

            if (result == null)
            {
                return false;
            }

            _dbContext.Remove(result);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  delete varient " + ex.Message);
            return false;
        }
    }
}