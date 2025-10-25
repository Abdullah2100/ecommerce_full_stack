using api.domain.entity;
using api.domain.Interface;
using api.util;
using ecommerc_dotnet.application;

namespace api.Infrastructure.Repositories;

public class OrderProductVariantRepository(AppDbContext context):IOrderProductVariant
{
    public void Add(List<OrderProductsVarient> entities)
    {
        foreach (var entity in entities)
        {
             context.OrdersProductsVarients.Add(new OrderProductsVarient()
            {
                Id = ClsUtil.GenerateGuid(),
                OrderItemId = entity.OrderItemId,
                ProductVariantId = entity.ProductVariantId,
            });
        }    
    }
}