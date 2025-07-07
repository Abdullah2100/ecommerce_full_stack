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
    private readonly IConfig _configuration;
    private readonly AppDbContext _dbContext;
    // private readonly ILogger _logger;

    public DeliveryData(AppDbContext appDbContext)
    {
        _dbContext = appDbContext;
    }




    public async Task<bool> isExistById(Guid id)
    {
        try
        {
            return await _dbContext.Deliveries.FindAsync(id)!=null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }

    public async Task<bool> isExistByUserId(Guid userID)
    {
        try
        {
            return await _dbContext
                .Deliveries
                .FirstOrDefaultAsync(de=>de.userId==userID)!=null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }
         
}