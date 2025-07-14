using System.Runtime.CompilerServices;
using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.dto.Response.delivery;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace ecommerc_dotnet.data;

public class OrderData
{
    public static  List<string> orderStatusDefination = new List<string>
        {
            "Regected",
            "Inprogress",
            "Excpected",
            "Inway",
            "Received",
            "Completed",
        };
        
    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;

    public OrderData(
        AppDbContext dbContext,
        IConfig config)
    {
        _dbContext = dbContext;
        _config = config;
    }

    public async Task<List<OrderDeliveryResponse>?> getOrderForDelivery(int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.userId equals user.id
                    select new OrderDeliveryResponse
                    {
                        id = order.id,
                        realPrice = _dbContext
                            .OrderItems
                            .Where(oi => oi.Status == enOrderItemStatus.Excepted)
                            .Select(oi => oi.price)
                            .ToList()
                            .Sum()
                        ,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        user_phone = user.phone,
                        status = order.status,
                        name = user.name,
                        totalPrice = order.totalPrice,
                        order_items = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.orderId == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.orderItemId == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        productVarientName = opv.productVarient.name,
                                        varientName = opv.productVarient.varient.name,
                                    }).ToList(),
                            }).ToList()
                    }
                )
                .AsNoTracking()
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


    public async Task<List<OrderResponseDto>?> getOrder(int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.userId equals user.id
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        userPhone = user.phone,
                        status = order.status,
                        deliveryFee = order.distanceFee,

                        name = user.name,
                        totalPrice = order.totalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.orderId == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.orderItemId == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        productVarientName = opv.productVarient.name,
                                        varientName = opv.productVarient.varient.name,
                                    }).ToList(),
                            }).ToList()
                    }
                )
                .AsNoTracking()
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

    public async Task<List<OrderItemResponseDto>?> getOrderItems
    (
        Guid storeId,
        int pagenumber,
        int pagesize = 25
    )
    {
        try
        {
            var result = await _dbContext.OrderItems
                .Include(oi => oi.orderProductsVarients)
                .Include(oi=>oi.order)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(oi => oi.id)
                .Where(oi => oi.storeId == storeId && oi.order.status>=2)
                .Select(oi => new OrderItemResponseDto
                {
                    id = oi.id,
                    quanity = oi.quanity,
                    orderId =oi.orderId,
                    product = _dbContext.Products.Where(ois => ois.id == oi.productId)
                        .Select(p => new OrderProductResponseDto
                        {
                            id = p.id,
                            name = p.name,
                            thmbnail = _config.getKey("url_file") + p.thmbnail,
                        }).FirstOrDefault(),
                    productVarient = _dbContext.OrdersProductsVarients
                        .AsNoTracking()
                        .Where(opv => opv.orderItemId == oi.id)
                        .Select(opv => new OrderVarientResponseDto
                        {
                            productVarientName = opv.productVarient.name,
                            varientName = opv.productVarient.varient.name,
                        }).ToList(),
                    orderItemStatus = oi.Status.ToString()
                })
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
    public async Task<OrderItemResponseDto?> getOrderItem
        (Guid id, Guid storeId)
    {
        try
        {
            var result = await _dbContext.OrderItems
                .Include(oi => oi.orderProductsVarients)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(oi => oi.id)
                .Where(oi => oi.id == id && oi.storeId == storeId)
                .Select(oi => new OrderItemResponseDto
                {
                    id = oi.id,
                    quanity = oi.quanity,
                    orderId = oi.orderId,
                    product = _dbContext.Products.Where(ois => ois.id == oi.productId)
                        .Select(p => new OrderProductResponseDto
                        {
                            id = p.id,
                            name = p.name,
                            thmbnail = _config.getKey("url_file") + p.thmbnail,
                        }).FirstOrDefault(),
                    productVarient = _dbContext.OrdersProductsVarients
                        .AsNoTracking()
                        .Where(opv => opv.orderItemId == oi.id)
                        .Select(opv => new OrderVarientResponseDto
                        {
                            productVarientName = opv.productVarient.name,
                            varientName = opv.productVarient.varient.name,
                        }).ToList(),
                    orderItemStatus = oi.Status.ToString()
                })
                .FirstOrDefaultAsync();
            return result;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }

    public async Task<List<OrderResponseDto>?> getOrder
    (
        Guid userid,
        int pagenumber,
        int pagesize = 25
    )
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.userId equals user.id
                    where user.id == userid
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        userPhone = user.phone,
                        status = order.status,
                        deliveryFee = order.distanceFee,
                        totalPrice = order.totalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.orderId == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                orderItemStatus = orIt.Status.ToString(),
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.orderItemId == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        productVarientName = opv.productVarient.name,
                                        varientName = opv.productVarient.varient.name,
                                    }).ToList(),
                            }).ToList()
                    }
                )
                .AsNoTracking()
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

    public async Task<bool> updateOrderItemStatus(Guid id, enOrderItemStatusDto status)
    {
        try
        {
             await _dbContext.OrderItems
                .Where(oi=>oi.id==id)
                .ExecuteUpdateAsync(
                    oi=>oi
                        .SetProperty(value=>value.Status,
                               status== enOrderItemStatusDto.Excepted ? enOrderItemStatus.Excepted
                                : enOrderItemStatus.Cancelled
                            )
                    );
             
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return false;
        }
    }


    public async Task<bool> deleteOrder(Guid userid, Guid orderId)
    {
        try
        {
            await _dbContext
                .Orders
                .Where(or => or.userId == userid && or.id == orderId)
                .ExecuteDeleteAsync();
            
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
                    join user in _dbContext.Users on order.userId equals user.id
                    where order.id == id
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        userPhone = user.phone,
                        status = order.status,
                        name = user.name,
                        deliveryFee = order.distanceFee,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.orderId == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {

                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        storeId = pr.storeId,
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.orderItemId == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        productVarientName = opv.productVarient.name,
                                        varientName = opv.productVarient.varient.name,
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

    public async Task<OrderResponseDto?> getOrder(Guid id, Guid userid)
    {
        try
        {
            return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.userId equals user.id
                    where order.id == id && user.id == userid
                    select new OrderResponseDto
                    {
                        id = order.id,
                        longitude = order.longitude,
                        latitude = order.latitude,
                        userPhone = user.phone,
                        status = order.status,
                        deliveryFee = order.distanceFee,

                        name = user.name,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.orderId == order.id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.price,
                                id = orIt.id,
                                quanity = orIt.quanity,
                                product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
                                    new OrderProductResponseDto
                                    {
                                        storeId = pr.storeId,
                                        id = pr.id,
                                        name = pr.name,
                                        thmbnail = _config.getKey("url_file") + pr.thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.orderItemId == orIt.id)
                                    .Select(opv => new OrderVarientResponseDto
                                    {
                                        productVarientName = opv.productVarient.name,
                                        varientName = opv.productVarient.varient.name,
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


    public async Task<int> getOrdersSize()
    {
        try
        {
            var result = await _dbContext
                .Orders
                .AsNoTracking()
                .CountAsync();
            return (int)Math.Ceiling((double)result / 25);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return 0;
        }
    }

    private async Task<bool> isSavedDistance(Guid orderId)
    {
        try
        {

            using (var command = _dbContext
            .Database
            .GetDbConnection()
            .CreateCommand())
            {
                command.CommandText = "SELECT * FROM fun_calculate_distance_between_user_and_stores(@orderId)";
                command.Parameters.Add(new NpgsqlParameter("@orderId", orderId));
                await _dbContext.Database.OpenConnectionAsync();
                var result = await command.ExecuteScalarAsync();
                return (bool?)result==true?true:false;
                
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from savedDistance " + ex.Message);
            return false;
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
                userId = userId,
                totalPrice = totalPrice,
                status = 1,
                createdAt = DateTime.Now,
                updatedAt = null,
            });

            items.ForEach(item =>
            {
                var orderItemId = clsUtil.generateGuid();
                _dbContext.OrderItems.AddAsync(new OrderItem
                {
                    id = orderItemId,
                    orderId = id,
                    productId = item.productId,
                    quanity = item.quanity,
                    storeId = item.storeId,
                    price = item.price,
                });
                item.productsVarientId.ForEach(pv =>
                {
                    _dbContext.OrdersProductsVarients.AddAsync(
                        new OrderProductsVarient
                        {
                            id = clsUtil.generateGuid(),
                            orderItemId = orderItemId,
                            productVarientId = pv
                        }
                    );
                });
            });

            await _dbContext.SaveChangesAsync();
            var result = await isSavedDistance(id);
            if (!result)
            {
                await deleteOrder(userId, id);
                return null;
            }
            return await getOrder(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from insert new Order  " + ex.Message);
            return null;
        }
    }

    public async Task<bool> updateOrderStatus(Guid orderId, int status)
    {
        try
        {
             await _dbContext
                .Orders
                .Where(o=>o.id==orderId)
                .ExecuteUpdateAsync(
                    o=>o.SetProperty(value=>value.status,status)
                    );
            
             await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from update order " + ex.Message);
            return false;
        }
    }
}