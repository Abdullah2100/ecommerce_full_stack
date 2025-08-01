using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace ecommerc_dotnet.infrastructure.repositories;

public class DeliveryRepository : IDeliveryRepository
{
    private readonly AppDbContext _context;

    public DeliveryRepository(AppDbContext context)
    {
        _context = context;
    }
    public async Task<IEnumerable<Delivery>> getAllAsync(int page, int length)
    {
        List<Delivery> deliveries = await _context
            .Deliveries
            .Include(de => de.User)
            .AsNoTracking()
            .ToListAsync();
        foreach (var delivery in deliveries)
        {
            delivery.Address = (await _context.Address
                .AsNoTracking()
                .FirstOrDefaultAsync(ad => ad.Id == delivery.Id));
        }

        return deliveries;
    }
    

    public async Task<int> addAsync(Delivery entity)
    {
        await _context.Address.AddAsync(entity.Address!);

        await _context.Deliveries.AddAsync(new Delivery
        {
            DeviceToken = entity.DeviceToken,
            Id = entity.Id,
            CreatedAt = DateTime.Now,
            UserId = entity.UserId,
            Thumbnail = entity.Thumbnail,
        });


        return await _context.SaveChangesAsync(); 
    }

    public async Task<int> updateAsync(Delivery entity)
    {
        
         _context.Address.Update(new Address
        {
            Id = entity.Address!.Id,
            Longitude = entity.Address!.Longitude,
            Latitude = entity.Address!.Latitude,
            Title = "my Place",
            CreatedAt = DateTime.Now,
            OwnerId = entity.UserId
        }); 
         _context.Deliveries.Update(new Delivery
         {
             DeviceToken = entity.DeviceToken,
             Id = entity.Id,
             CreatedAt = DateTime.Now,
             UserId = entity.UserId,
             Thumbnail = entity.Thumbnail,
         });
         return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        Delivery? entity = await _context.Deliveries.FindAsync(id);
        if (entity is null) return 0;
        entity.IsBlocked = !entity.IsBlocked;
        return  await _context.SaveChangesAsync();

    }

    public async Task<Delivery?> getDelivery(Guid id)
    {
        return (await _context
            .Deliveries
            .Include(de => de.User)
            .FirstOrDefaultAsync(de => de.Id == id));
    }

    public async Task<Delivery?> getDeliveryByUserId(Guid userId)
    {
        return (await _context
            .Deliveries
            .Include(de => de.User)
            .FirstOrDefaultAsync(de => de.UserId == userId));
    }

    public async Task<DeliveryAnalysDto> getDeliveryAnalys(Guid id)
    {
        using (var cmd = _context.Database.GetDbConnection().CreateCommand())
        {
            cmd.CommandText = "SELECT * FROM get_delivery_fee_info(@deliveryId)";
            cmd.Parameters.Add(new NpgsqlParameter("@deliveryId", id));
            await _context.Database.OpenConnectionAsync();
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

 

    public async Task<bool> isExistByUserId(Guid userId)
    {
        return await _context
            .Deliveries
            .AsNoTracking()
            .AnyAsync(de=>de.UserId == userId);
    }
}