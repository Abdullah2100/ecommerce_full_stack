using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class OrderData
{
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;

    public OrderData(
        AppDbContext dbContext,
        IConfig config)
    {
        _dbContext = dbContext;
        _config = config;
    }

    public async Task<List<OrderResponseDto>?> getOrder(int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.user_id equals user.ID
                select new OrderResponseDto
                {
                    id = order.id,
                    longitude = order.longitude,
                    latitude = order.latitude,
                    user_phone = user.phone,
                    status = order.status,
                    order_items = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.order_id == order.id)
                        .Select(orIt => new OrderItemResponseDto
                        {
                            price = orIt.price,
                            id = orIt.id,
                            quanity = orIt.quanity,
                            product = _dbContext.Products.Where(p => p.id == orIt.product_id).Select(pr =>
                                new OrderProductResponseDto
                                {
                                    id = pr.id,
                                    name = pr.name,
                                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                }).FirstOrDefault(),
                            productVarient = _dbContext.OrdersProductsVarients
                                .AsNoTracking()
                                .Where(opv => opv.order_item_id == orIt.id)
                                .Select(opv => new OrderVarientResponseDto
                                {
                                    product_varient_name = opv.productVarient.name,
                                    varient_name = opv.productVarient.varient.name,
                                }).ToList(),
                        }).ToList()
                }
            ).AsNoTracking()
            .Skip((pagenumber - 1) * pagesize)
            .Take(pagesize)
            .ToListAsync();
            return result;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }
    public async Task<List<OrderResponseDto>?> getOrder(Guid userid, int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.user_id equals user.ID
                where user.ID == userid
                select new OrderResponseDto
                {
                    id = order.id,
                    longitude = order.longitude,
                    latitude = order.latitude,
                    user_phone = user.phone,
                    status = order.status,
                    order_items = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.order_id == order.id)
                        .Select(orIt => new OrderItemResponseDto
                        {
                            price = orIt.price,
                            id = orIt.id,
                            quanity = orIt.quanity,
                            product = _dbContext.Products.Where(p => p.id == orIt.product_id).Select(pr =>
                                new OrderProductResponseDto
                                {
                                    id = pr.id,
                                    name = pr.name,
                                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                }).FirstOrDefault(),
                            productVarient = _dbContext.OrdersProductsVarients
                                .AsNoTracking()
                                .Where(opv => opv.order_item_id == orIt.id)
                                .Select(opv => new OrderVarientResponseDto
                                {
                                    product_varient_name = opv.productVarient.name,
                                    varient_name = opv.productVarient.varient.name,
                                }).ToList(),
                        }).ToList()
                }
            ).AsNoTracking()
            .Skip((pagenumber - 1) * pagesize)
            .Take(pagesize)
            .ToListAsync();
            return result;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }

  public async Task<bool> deleteOrder(Guid userid, Guid order_id)
    {
        try
        {
              var order =  await _dbContext.Orders.FirstOrDefaultAsync(or=>or.user_id==userid&&or.id==order_id);
            if (order == null) return false;
            _dbContext.Orders.Remove(order);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return false;
        }
    }

    public async Task<OrderResponseDto?> getOrder(Guid id)
    {
        try
        {
            return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.user_id equals user.ID
                    where order.id == id
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        user_phone = user.phone,
                        status = order.status,
                        order_items = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.order_id == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.product_id).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                      .Where(opv => opv.order_item_id == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        product_varient_name = opv.productVarient.name,
                                        varient_name = opv.productVarient.varient.name,
                                    }).ToList(),
                            }).ToList()
                    }
                ).AsNoTracking()
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }
  public async Task<OrderResponseDto?> getOrder(Guid id,Guid userid)
    {
        try
        {
            return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.user_id equals user.ID
                    where order.id == id && user.ID ==userid
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        user_phone = user.phone,
                        status = order.status,
                        order_items = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.order_id == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.product_id).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                      .Where(opv => opv.order_item_id == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        product_varient_name = opv.productVarient.name,
                                        varient_name = opv.productVarient.varient.name,
                                    }).ToList(),
                            }).ToList()
                    }
                ).AsNoTracking()
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }


    public async Task<OrderResponseDto?> createOrder
    (
        Guid userId,
        decimal longitude,
        decimal latitude,
        decimal totalPrice,
        List<OrderRequestItemsDto> items
    )
    {
        try
        {
            var id = clsUtil.generateGuid();
            await _dbContext.Orders.AddAsync(new Order
            {
                id = id,
                longitude = longitude,
                latitude = latitude,
                user_id = userId,
                totalPrice = totalPrice,
                status = 1,
                created_at = DateTime.Now,
                updated_at = null
            });

            items.ForEach(item =>
            {
                var orderItemId = clsUtil.generateGuid();
                _dbContext.OrderItems.AddAsync(new OrderItem
                {
                    id = orderItemId,
                    order_id = id,
                    product_id = item.product_Id,
                    quanity = item.quanity,
                    store_id = item.store_id,
                    price = item.price,
                });
                item.products_varient_id.ForEach(pv =>
                {
                    _dbContext.OrdersProductsVarients.AddAsync(
                        new OrderProductsVarient
                        {
                            id = clsUtil.generateGuid(),
                            order_item_id = orderItemId,
                            product_varient_id = pv
                        }
                    );
                });
            });

            await _dbContext.SaveChangesAsync();
            return await getOrder(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from insert new Order  " + ex.Message);
            return null;
        }
    }
}