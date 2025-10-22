using ecommerc_dotnet.application;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.repositories;

public class OrderProductVariantRepository(AppDbContext context):IOrderProductVariant
{
    public void add(List<OrderProductsVarient> entities)
    {
        foreach (var entity in entities)
        {
             context.OrdersProductsVarients.Add(new OrderProductsVarient()
            {
                Id = clsUtil.generateGuid(),
                OrderItemId = entity.OrderItemId,
                ProductVarientId = entity.ProductVarientId,
            });
        }    
    }
}