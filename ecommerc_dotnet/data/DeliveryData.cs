using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace ecommerc_dotnet.data;

public class DeliveryData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;

    public DeliveryData(AppDbContext appDbContext, IConfig config)
    {
        _dbContext = appDbContext;
        _config = config;
    }


    public async Task<bool> isExistById(Guid id)
    {
        try
        {
            return await _dbContext.Deliveries.FindAsync(id) != null;
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
                .FirstOrDefaultAsync(de => de.userId == userId) != null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }


    public async Task<bool?> updateDeliveryDeviceToken(
        Guid id,
        string? deviceToken)
    {
        try
        {
            await _dbContext.Deliveries
                .Where(de => de.userId == id)
                .ExecuteUpdateAsync(de => de.SetProperty(value => value.deviceToken, deviceToken));

            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from update User" + e.Message);
            return null;
        }
    }


    public async Task<bool?> createDelivery(
        Guid userId,
        string deviceToken,
        decimal longitude,
        decimal latitude,
        string? thumbnail = null
    )
    {
        try
        {
            var addressId = clsUtil.generateGuid();
            var id = clsUtil.generateGuid();
            await _dbContext.Address.AddAsync(new Address
            {
                id = addressId,
                longitude = longitude,
                latitude = latitude,
                title = "my Place",
                createdAt = DateTime.Now,
                ownerId = id
            });

            await _dbContext.Deliveries.AddAsync(new Delivery
            {
                deviceToken = deviceToken,
                id = id,
                createdAt = DateTime.Now,
                userId = userId,
            });

            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this error message from create new delivery {ex.Message}");
            return null;
        }
    }


    public async Task<DeliveryInfoResponseDto?> getInfoByUserId(Guid id)
    {
        try
        {
            DeliveryInfoResponseDto? response = await (
                    from deliver in _dbContext.Deliveries
                    join user in _dbContext.Users on deliver.userId equals user.id
                    join address in _dbContext.Address on deliver.userId equals address.ownerId
                    where deliver.userId == id
                    select new DeliveryInfoResponseDto
                    {
                        userId = user.id,
                        id = deliver.id,
                    }
                ).AsNoTracking()
                .FirstOrDefaultAsync();

            return response;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this error from getUserById {ex.Message}");
            return null;
        }
    }


    public async Task<DeliveryInfoResponseDto?> getInfoById(Guid id)
    {
        try
        {
            DeliveryInfoResponseDto? response = await (
                    from deliver in _dbContext.Deliveries
                    join user in _dbContext.Users on deliver.userId equals user.id
                    join address in _dbContext.Address on deliver.userId equals address.ownerId
                    where deliver.id ==id
                    select new DeliveryInfoResponseDto
                    {
                        userId = user.id,
                        id = deliver.id,
                        createdAt = deliver.createdAt,
                        address = new AddressResponseDto
                        {
                            longitude = address.longitude,
                            latitude = address.latitude,
                            title = address.title,
                        },
                        thumbnail = deliver.thumbnail != null ? _config.getKey("url_file") + deliver.thumbnail : null,
                        isAvaliable = deliver.isAvaliable,
                        user= new UserDeliveryInfoResponseDto 
                        {
                            email = user.email,
                            phone = user.phone,
                            thumbnail = user.thumbnail != null ? _config.getKey("url_file") + user.thumbnail : null,
                            name= user.name,
                        }
                    }
                ).AsNoTracking()
                .FirstOrDefaultAsync();
            if (response != null)
            {
                response.analys = await getDeliveryAnalys(response.id);
            }

            return response;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this error from getUserById {ex.Message}");
            return null;
        }
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
                using (var reader =await cmd.ExecuteReaderAsync())
                {
                    if (reader.HasRows)
                    {
                        if (reader.Read())
                        {
                            info = new DeliveryAnalysDto
                            {
                                dayFee = (decimal?)reader["dayFee"],
                                weekFee = (decimal?)reader["weekFee"],
                                monthFee = (decimal?)reader["monthFee"],
                                dayOrder = (int)reader["dayorder"],
                                weekOrder = (int)reader["weekorder"]
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