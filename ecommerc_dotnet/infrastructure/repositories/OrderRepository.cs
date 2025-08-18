using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.dto;
using hotel_api.util;
using Npgsql;

namespace ecommerc_dotnet.infrastructure.repositories;



public class OrderRepository(AppDbContext context) : IOrderRepository
{
    public async Task<IEnumerable<Order>> getOrders(
        Guid userId,
        int pageNum,
        int pageSize
    )
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.UserId == userId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<Order?> getOrder(Guid id)
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id);
    }

    public async Task<Order?> getOrder(Guid id, Guid userId)
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id && o.UserId == userId);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context.Orders
            .AsNoTracking()
            .AnyAsync(o => o.Id == id);
    }

    public async Task<bool> isCanCancelOrder(Guid id)
    {
        return await context
            .OrderItems
            .AsNoTracking()
            .AnyAsync(i => i.OrderId == id && i.Status == enOrderItemStatus.RecivedByDelivery
            );
    }

    public async Task<bool> isValidTotalPrice(decimal totalPrice, List<CreateOrderItemDto> items)
    {
        bool isAmbiguous = false;
        decimal realPrice = 0;

        foreach (var item in items)
        {
            var product = await context.Products.FindAsync(item.ProductId);
            decimal varientPrice = 1;

            item.ProductsVarientId?.ForEach(pvi =>
            {
                var productVairntPrice = context.ProductVarients
                    .FirstOrDefault(pv => pv.ProductId == product!.Id && pv.Id == pvi);
                if (productVairntPrice is null)
                {
                    isAmbiguous = true;
                    return;
                }

                varientPrice = varientPrice * productVairntPrice.Precentage;
            });
            if (isAmbiguous == true)
            {
                break;
            }

            if (product?.Price != item.Price)
            {
                isAmbiguous = true;
                break;
            }

            realPrice += ((varientPrice * product.Price) * item.Quantity);
        }


        if (isAmbiguous)
        {
            return false;
        }

        return realPrice == totalPrice;
    }

    public async Task<IEnumerable<Order>> getOrderNoBelongToAnyDelivery(int pageNum, int pageSize)
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.DeleveryId == null)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Order>> getOrderBelongToDelivery(Guid deliveryId, int pageNum, int pageSize)
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.DeleveryId == deliveryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<int> removeOrderFromDelivery(Guid id, Guid deliveryId)
    {
        Order? result = await context
            .Orders
            .FirstOrDefaultAsync(o => o.Id == id && o.DeleveryId == deliveryId);

        if (result == null) return 0;

        result.DeleveryId = null;

        return await context.SaveChangesAsync();
    }

    public async Task<IEnumerable<Order>> getAllAsync(int page, int length)
    {
        return await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    private async Task<bool> isSavedDistance(Guid orderId)
    {
        using (var command = context
                   .Database
                   .GetDbConnection()
                   .CreateCommand())
        {
            command.CommandText = "SELECT * FROM fun_calculate_distance_between_user_and_stores(@orderId)";
            command.Parameters.Add(new NpgsqlParameter("@orderId", orderId));
            await context.Database.OpenConnectionAsync();
            var result = await command.ExecuteScalarAsync();
            return (bool?)result == true ? true : false;
        }
    }


    public async Task<int> addAsync(Order entity)
    {
        await context.Orders.AddAsync(new Order
        {
            Id = entity.Id,
            Longitude = entity.Longitude,
            Latitude = entity.Latitude,
            UserId = entity.UserId,
            TotalPrice = entity.TotalPrice,
            Status = 1,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
        });

        foreach (var orderItem in entity.Items)
        {
            var orderItemId = clsUtil.generateGuid();
            await context.OrderItems.AddAsync(new OrderItem
            {
                Id = orderItemId,
                OrderId = entity.Id,
                ProductId = orderItem.ProductId,
                Quanity = orderItem.Quanity,
                StoreId = orderItem.StoreId,
                Price = orderItem.Price,
            });
            if (orderItem.OrderProductsVarients is not null)
                foreach (var orderProductVarient in orderItem.OrderProductsVarients)
                {
                    await context.OrdersProductsVarients.AddAsync(new OrderProductsVarient()
                    {
                        Id = clsUtil.generateGuid(),
                        OrderItemId = orderItemId,
                        ProductVarientId = orderProductVarient.ProductVarientId,
                    });
                }
        }

        int result = await context.SaveChangesAsync();
        if (result == 0) return 0;
        result = (await isSavedDistance(entity.Id)) == true ? 1 : 0;
        if (result == 0)
        {
            await deleteAsync(entity.Id);
            return 0;
        }

        return 1;
    }

    public Task<int> updateAsync(Order entity)
    {
        context.Orders.Update(entity);
        return context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await context.Orders.Where(o => o.Id == id).ExecuteDeleteAsync();
        return await context.SaveChangesAsync();
    }
    
    //orderitmes
    
    public async Task<int> updateOrderItemStatus(Guid id, enOrderItemStatus status)
    {
        OrderItem? orderItem = await context.OrderItems.FindAsync(id);
        if (orderItem is null) return 0;
        orderItem.Status = status;
        return await context.SaveChangesAsync();
    }
    
    
    public async Task<IEnumerable<OrderItem>> getOrderItems(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        return await context.OrderItems
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVarients)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.StoreId == storeId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<OrderItem?> getOrderItem(Guid id, Guid storeId)
    {
        return await context.OrderItems
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVarients)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id && o.StoreId == storeId);
    }

    public async Task<OrderItem?> getOrderItem(Guid id)
    {
        return await context.OrderItems
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVarients)
            .Include(oi => oi.Store)
            .Include(oi=>oi.Order)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id);

    }


}