using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class  DeliveryData
{
    private readonly AppDbContext _dbContext;

    public DeliveryData(AppDbContext appDbContext)
    {
        _dbContext = appDbContext;
    }




    public async Task<bool> isExistById(Guid id)
    {
        try
        {
            return await _dbContext.Deliveries.FindAsync(id)!= null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }

    public async Task<bool> isExistByUserId(Guid userId)
    {
        try
        {
            return await _dbContext
                .Deliveries
                .AsNoTracking()
                .FirstOrDefaultAsync(de=>de.userId==userId)!= null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }
         
}