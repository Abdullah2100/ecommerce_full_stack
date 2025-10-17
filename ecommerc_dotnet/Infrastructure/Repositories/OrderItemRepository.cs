using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.domain.entity;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.Presentation.dto;
using hotel_api.util;
using Npgsql;

namespace ecommerc_dotnet.infrastructure.repositories;

public class OrderItemRepository(AppDbContext context) :IOrderItemRepository 
{
  

    //orderitmes
    

    public void  updateOrderItemStatus(Guid id, enOrderItemStatus status)
    {
        OrderItem? orderItem =  context.OrderItems.Find(id);
        if (orderItem is null) throw new ArgumentNullException();
        orderItem.Status = status;
    }




    public async Task<IEnumerable<OrderItem>> getOrderItems(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        return await context.OrderItems
            .Include(oi => oi.Order)
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
            .Include(oi => oi.Order)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id);
    }

    public void add(OrderItem entity)
    {
        context.OrderItems.Add(entity:entity);
    }

    public void update(OrderItem entity)
    {
        context.Update(entity:entity);
    }
}