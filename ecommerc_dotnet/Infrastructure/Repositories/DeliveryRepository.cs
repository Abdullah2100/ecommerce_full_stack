using ecommerc_dotnet.application;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.dto;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace ecommerc_dotnet.infrastructure.repositories;

public class DeliveryRepository(
    AppDbContext context
    ) : IDeliveryRepository
{

    

    public void  add(Delivery entity)
    {
       //  context.Address.AddAsync(entity.Address!);

         context.Deliveries.Add(new Delivery
        {
            DeviceToken = entity.DeviceToken,
            Id = entity.Id,
            CreatedAt = DateTime.Now,
            UserId = entity.UserId,
            Thumbnail = entity.Thumbnail,
            BelongTo = entity.BelongTo
        });


    }

    public void  update(Delivery entity)
    {
 
            context.Deliveries.Update(new Delivery
            {
                DeviceToken = entity.DeviceToken,
                Id = entity.Id,
                CreatedAt = DateTime.Now,
                UserId = entity.UserId,
                Thumbnail = entity.Thumbnail,
            });
       
    }

    public void  delete(Guid id)
    {
        Delivery? entity = context.Deliveries.Find(id);
        if (entity is null)throw new  ArgumentNullException();
        entity.IsBlocked = !entity.IsBlocked;
    }

    public async Task<Delivery?> getDelivery(Guid id)
    {
        var delivery =(await context
            .Deliveries
            .Include(de => de.User)
            .AsNoTracking()
            .FirstOrDefaultAsync(de => de.Id == id));
        if (delivery is null) return null;
        delivery.Address = await context.Address.FirstOrDefaultAsync(ad => ad.OwnerId == delivery.Id);
        return delivery;
    }

    public async Task<Delivery?> getDeliveryByUserId(Guid userId)
    {
        var delivery= (await context
            .Deliveries
            .AsNoTracking()
            .Include(de => de.User)
            .FirstOrDefaultAsync(de => de.UserId == userId));
        
        if (delivery is null) return null;
        delivery.Address = await context.Address.FirstOrDefaultAsync(ad => ad.OwnerId == delivery.Id);
        return delivery; 
    }

    public async Task<List<Delivery>?> getDeliveriesByBelongTo(Guid belongToId,int page, int size)
    {
        List<Delivery> deliveries = await context
            .Deliveries
            .Include(de => de.User)
            .AsNoTracking()
            .Take(page)
            .Skip((page - 1) * size)
            .ToListAsync();
        foreach (var delivery in deliveries)
        {
            delivery.Address = (await context.Address
                .AsNoTracking()
                .FirstOrDefaultAsync(ad => ad.Id == delivery.Id));
        }

        return deliveries;
    }

    public async Task<List<Delivery>?> getDeliveries()
    {
        var delivery= (await context
            .Deliveries
            .AsNoTracking()
            .Include(de => de.User).ToListAsync());
        
        return delivery;  
    }

    public async Task<DeliveryAnalysDto> getDeliveryAnalys(Guid id)
    {
        using (var cmd = context.Database.GetDbConnection().CreateCommand())
        {
            cmd.CommandText = "SELECT * FROM get_delivery_fee_info(@deliveryId)";
            cmd.Parameters.Add(new NpgsqlParameter("@deliveryId", id));
            await context.Database.OpenConnectionAsync();
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
        return await context
            .Deliveries
            .AsNoTracking()
            .AnyAsync(de => de.UserId == userId);
    }
}