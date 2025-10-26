using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class OrderItemRepository(AppDbContext context) :IOrderItemRepository 
{
  

    //orderitmes
    




    public async Task<IEnumerable<OrderItem>> GetOrderItems(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        return await context.OrderItems
            .Include(oi => oi.Order)
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVariants)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.StoreId == storeId && ((int)o.Order.Status)>1 )
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<OrderItem?> GetOrderItem(Guid id, Guid storeId)
    {
        return await context.OrderItems
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVariants)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id && o.StoreId == storeId);
    }

    public async Task<OrderItem?> GetOrderItem(Guid id)
    {
        return await context.OrderItems
            .Include(oi => oi.Product)
            .Include(oi => oi.OrderProductsVariants)
            .Include(oi => oi.Store)
            .Include(oi => oi.Order)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id);
    }

    public void Add(OrderItem entity)
    {
        context.OrderItems.Add(entity:entity);
    }

    public void Update(OrderItem entity)
    {
        context.Update(entity:entity);
    }
}