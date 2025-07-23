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
            Varient? result = await _dbContext
                .Varients
                .FindAsync(id);
            if (result is null) return null;
            return new VarientResposeDto
            {
                id = result.Id,
                name = result.Name,
            };
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return null;
        }
    }

    public async Task<List<VarientResposeDto>> getVarients(int pageNumber, int pageSize=25)
    {
        try
        {
            return await _dbContext
                .Varients
                .AsNoTracking()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(var => new VarientResposeDto
                    {
                        id = var.Id,
                        name = var.Name,

                    }
                ).ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return new List<VarientResposeDto>();
        }
    }

    public async Task<double> getVarients()
    {
        try
        {
            var result=(await _dbContext
                .Varients
                .AsNoTracking()
                .CountAsync());
            return (double) result / 25;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this exception occured getting varient data " + ex.Message);
            return 0;
        }
    }
 
    public async Task<bool> isExist(string name)
    {
        try
        {
            var result = await _dbContext
                .Varients
                .AsNoTracking()
                .FirstOrDefaultAsync(va => va.Name == name);
            return result !=  null;
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
            var result = await _dbContext
                .Varients
                .AsNoTracking()
                .FirstOrDefaultAsync(va => va.Id == id);
            return result !=  null;
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
            await _dbContext
                .Varients
                .AddAsync(new Varient { Id = id, Name = name });
            await _dbContext
                .SaveChangesAsync();
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
            await _dbContext
                .Varients.Where(v=>v.Id==id)
                .ExecuteUpdateAsync(v=>v
                    .SetProperty(value=>value.Name,name));

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
        Guid varrientId)
    {
        try
        {
             await _dbContext
                 .Varients
                 .Where(v=>v.Id==varrientId)
                 .ExecuteDeleteAsync();
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