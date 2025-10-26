using api.application;
using api.domain.entity;
using api.domain.Interface;
using api.util;

namespace api.Infrastructure.Repositories;

public class OrderProductVariantRepository(AppDbContext context):IOrderProductVariant
{
    public void Add(List<OrderProductsVariant> entities)
    {
        foreach (var entity in entities)
        {
             context.OrdersProductsVarients.Add(new OrderProductsVariant()
            {
                Id = ClsUtil.GenerateGuid(),
                OrderItemId = entity.OrderItemId,
                ProductVariantId = entity.ProductVariantId,
            });
        }    
    }
}