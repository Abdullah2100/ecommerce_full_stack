using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace ecommerc_dotnet.data;

public class DeliveryData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IUnitOfWork _unitOfWork;

    public DeliveryData(
        AppDbContext appDbContext,
        IConfig config,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = appDbContext;
        _config = config;
        _unitOfWork = unitOfWork;
    }


    public async Task<bool> isExistById(Guid id)
    {
        return await _dbContext.Deliveries
            .FindAsync(id) != null;
    }

    public async Task<bool> isExistByUserId(Guid userId)
    {
        return await _dbContext
            .Deliveries
            .AsNoTracking()
            .AnyAsync(de => de.UserId == userId);
    }


    public async Task<bool> updateDeviceToken(
        Guid id,
        string? deviceToken)
    {
        await _dbContext.Deliveries
            .Where(de => de.UserId == id)
            .ExecuteUpdateAsync(de => de.SetProperty(value => value.deviceToken, deviceToken));

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;

        return true;
    }


    public async Task<bool> updateStatus(
        Guid id,
        bool isAviable)
    {
        await _dbContext.Deliveries
            .Where(de => de.Id == id)
            .ExecuteUpdateAsync(de =>
                de.SetProperty(value => value.IsAvaliable, isAviable));
        
        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;

        return true;
    }


    public async Task<bool?> createDelivery(
        Guid userId,
        string deviceToken,
        decimal longitude,
        decimal latitude,
        string? thumbnail = null
    )
    {
        var addressId = clsUtil.generateGuid();
        var id = clsUtil.generateGuid();
        await _dbContext.Address.AddAsync(new Address
        {
            Id = addressId,
            Longitude = longitude,
            Latitude = latitude,
            Title = "my Place",
            CreatedAt = DateTime.Now,
            OwnerId = id
        });

        await _dbContext.Deliveries.AddAsync(new Delivery
        {
            deviceToken = deviceToken,
            Id = id,
            CreatedAt = DateTime.Now,
            UserId = userId,
            Thumbnail = thumbnail
        });

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }


    public async Task<DeliveryDto?> getInfoByUserId(Guid id)
    {
       
            

            DeliveryDto? delivery = (await _dbContext
                .Deliveries
                .Include(de=>de.User)
                .FirstOrDefaultAsync(de=>de.UserId==id))?
                .toDto(_config.getKey("url_file"));

            if (delivery == null) return null;
            
            delivery.Address = (await _dbContext.Address
                .AsNoTracking() 
                .FirstOrDefaultAsync(ad => ad.Id==id))
                ?.toDeliveryDto();
            delivery.Analys = await getDeliveryAnalys(id);

            return delivery;

    }


    public async Task<DeliveryDto?> getInfoById(Guid id)
    {
     
        DeliveryDto? delivery = (await _dbContext
                .Deliveries
                .Include(de=>de.User)
                .FirstOrDefaultAsync(de=>de.Id==id))?
            .toDto(_config.getKey("url_file"));

        if (delivery == null) return null;
            
        delivery.Address = (await _dbContext.Address
                .AsNoTracking() 
                .FirstOrDefaultAsync(ad => ad.Id==id))
            ?.toDeliveryDto();
        delivery.Analys = await getDeliveryAnalys(id);

        return delivery; 
    }


    private async Task<DeliveryAnalysDto?> getDeliveryAnalys(Guid id)
    {
        try
        {
            using (var cmd = _dbContext.Database.GetDbConnection().CreateCommand())
            {
                cmd.CommandText = "SELECT * FROM get_delivery_fee_info(@deliveryId)";
                cmd.Parameters.Add(new NpgsqlParameter("@deliveryId", id));
                await _dbContext.Database.OpenConnectionAsync();
                DeliveryAnalysDto? info = null;
                using (var reader = await cmd.ExecuteReaderAsync())
                {
                    if (reader.HasRows)
                    {
                        if (reader.Read())
                        {
                            info = new DeliveryAnalysDto
                            {
                                DayFee = (decimal?)reader["dayFee"],
                                WeekFee = (decimal?)reader["weekFee"],
                                MonthFee = (decimal?)reader["monthFee"],
                                DayOrder = (int)reader["dayorder"],
                                WeekOrder = (int)reader["weekorder"]
                            };
                        }
                    }
                }

                return info;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this error from getDeliveryAnalys {ex.Message}");
            return null;
        }
    }
    
}