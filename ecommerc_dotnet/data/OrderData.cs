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
    public static List<string> orderStatusDefination = new List<string>
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

    // public async Task<List<OrderDeliveryResponse>?> getOrderForDelivery(int pagenumber, int pagesize = 25)
    // {
    //     try
    //     {
    //         var result = await (
    //                 from order in _dbContext.Orders
    //                 join user in _dbContext.Users on order.userId equals user.id
    //                 select new OrderDeliveryResponse
    //                 {
    //                     id = order.id,
    //                     realPrice = _dbContext
    //                         .OrderItems
    //                         .Where(oi => oi.Status == enOrderItemStatus.Excepted)
    //                         .Select(oi => oi.price)
    //                         .ToList()
    //                         .Sum()
    //                     ,
    //                     longitude = order.longitude,
    //                     latitude = order.latitude,
    //                     user_phone = user.phone,
    //                     status = order.status,
    //                     name = user.name,
    //                     totalPrice = order.totalPrice,
    //                     order_items = _dbContext.OrderItems
    //                         .AsNoTracking()
    //                         .Where(oi => oi.orderId == order.id)
    //                         .Select(orIt => new OrderItemResponseDto
    //                         {
    //                             price = orIt.price,
    //                             id = orIt.id,
    //                             quanity = orIt.quanity,
    //                             product = _dbContext.Products.Where(p => p.id == orIt.productId).Select(pr =>
    //                                 new OrderProductResponseDto
    //                                 {
    //                                     id = pr.id,
    //                                     name = pr.name,
    //                                     thmbnail = _config.getKey("url_file") + pr.thmbnail,
    //                                 }).FirstOrDefault(),
    //                             productVarient = _dbContext.OrdersProductsVarients
    //                                 .AsNoTracking()
    //                                 .Where(opv => opv.orderItemId == orIt.id)
    //                                 .Select(opv => new OrderVarientResponseDto
    //                                 {
    //                                     productVarientName = opv.productVarient.name,
    //                                     varientName = opv.productVarient.varient.name,
    //                                 }).ToList(),
    //                         }).ToList()
    //                 }
    //             )
    //             .AsNoTracking()
    //             .Skip((pagenumber - 1) * pagesize)
    //             .Take(pagesize)
    //             .ToListAsync();
    //         return result;
    //     }
    //     catch (Exception ex)
    //     {
    //         Console.WriteLine("this excption from getting orders List " + ex.Message);
    //         return null;
    //     }
    // }
    //

    public async Task<List<OrderResponseDto>?> getOrder(int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    select new OrderResponseDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        deliveryFee = order.DistanceFee,

                        name = user.Name,
                        totalPrice = order.TotalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
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
                .Include(oi => oi.OrderProductsVarients)
                .Include(oi => oi.Order)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(oi => oi.Id)
                .Where(oi => oi.StoreId == storeId && oi.Order.Status >= 2)
                .Select(oi => new OrderItemResponseDto
                {
                    id = oi.Id,
                    quanity = oi.Quanity,
                    orderId = oi.OrderId,
                    product = _dbContext.Products.Where(ois => ois.Id == oi.ProductId)
                        .Select(p => new OrderProductDto
                        {
                            Id = p.Id,
                            Name = p.Name,
                            Thmbnail = _config.getKey("url_file") + p.Thmbnail,
                        }).FirstOrDefault(),
                    productVarient = _dbContext.OrdersProductsVarients
                        .AsNoTracking()
                        .Where(opv => opv.OrderItemId == oi.Id)
                        .Select(opv => new OrderVarientDto
                        {
                            ProductVarientName = opv.ProductVarient.Name,
                            VarientName = opv.ProductVarient.varient.Name,
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
                .Include(oi => oi.OrderProductsVarients)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(oi => oi.Id)
                .Where(oi => oi.Id == id && oi.StoreId == storeId)
                .Select(oi => new OrderItemResponseDto
                {
                    id = oi.Id,
                    quanity = oi.Quanity,
                    orderId = oi.OrderId,
                    product = _dbContext.Products.Where(ois => ois.Id == oi.ProductId)
                        .Select(p => new OrderProductDto
                        {
                            Id = p.Id,
                            Name = p.Name,
                            Thmbnail = _config.getKey("url_file") + p.Thmbnail,
                        }).FirstOrDefault(),
                    productVarient = _dbContext.OrdersProductsVarients
                        .AsNoTracking()
                        .Where(opv => opv.OrderItemId == oi.Id)
                        .Select(opv => new OrderVarientDto
                        {
                            ProductVarientName = opv.ProductVarient.Name,
                            VarientName = opv.ProductVarient.varient.Name,
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
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where user.Id == userid
                    select new OrderResponseDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        deliveryFee = order.DistanceFee,
                        totalPrice = order.TotalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                orderItemStatus = orIt.Status.ToString(),
                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
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
                .Where(oi => oi.Id == id)
                .ExecuteUpdateAsync(
                    oi => oi
                        .SetProperty(value => value.Status,
                            status == enOrderItemStatusDto.Excepted ? enOrderItemStatus.Excepted
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
                .Where(or => or.UserId == userid && or.Id == orderId)
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

    public async Task<bool?> isExist(Guid id)
    {
        try
        {
            Order? order = await _dbContext.Orders.FindAsync(id);
            
            return order != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }

   public async Task<Order?> getOrderById(Guid id)
    {
        try
        {
            Order? order = await _dbContext.Orders.FindAsync(id);

            if (order == null) return null;
            return order;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from getting orders List " + ex.Message);
            return null;
        }
    }


    public async Task<OrderResponseDto?> getOrder(Guid id)
    {
        try
        {
            return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.Id == id
                    select new OrderResponseDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        name = user.Name,
                        deliveryFee = order.DistanceFee,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDto
                            {

                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        StoreId = pr.StoreId,
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
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
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.Id == id && user.Id == userid
                    select new OrderResponseDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        deliveryFee = order.DistanceFee,

                        name = user.Name,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDto
                            {
                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        StoreId = pr.StoreId,
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
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
                return (bool?)result == true ? true : false;

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
                Id = id,
                Longitude = longitude,
                Latitude = latitude,
                UserId = userId,
                TotalPrice = totalPrice,
                Status = 1,
                CreatedAt = DateTime.Now,
                UpdatedAt = null,
            });

            items.ForEach(item =>
            {
                var orderItemId = clsUtil.generateGuid();
                _dbContext.OrderItems.AddAsync(new OrderItem
                {
                    Id = orderItemId,
                    OrderId = id,
                    ProductId = item.productId,
                    Quanity = item.quanity,
                    StoreId = item.storeId,
                    Price = item.price,
                });
                item.productsVarientId.ForEach(pv =>
                {
                    _dbContext.OrdersProductsVarients.AddAsync(
                        new OrderProductsVarient
                        {
                            Id = clsUtil.generateGuid(),
                            OrderItemId = orderItemId,
                            ProductVarientId = pv
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
                .Where(o => o.Id == orderId)
                .ExecuteUpdateAsync(
                    o => o.SetProperty(value => value.Status, status)
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





    //delivery
    public async Task<List<OrderResponseDeliveryDto>?> getOrdersNotBelongToDeliveries(int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.DeleveryId == null && order.Status > 1
                    select new OrderResponseDeliveryDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        deliveryFee = order.DistanceFee,
                        realPrice = _dbContext.OrderItems
                            .Where(oi => oi.OrderId == order.Id && oi.Status == enOrderItemStatus.Excepted)
                            .Sum(oi => oi.Quanity),
                        name = user.Name,
                        totalPrice = order.TotalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDeliveryDto
                            {
                                address = _dbContext.Address
                                    .AsNoTracking()
                                    .Where(st => st.Id == orIt.StoreId)
                                    .Select(ad => new AddressResponseDeliveryDto
                                    {
                                        longitude = ad.Longitude,
                                        latitude = ad.Latitude,
                                    }).FirstOrDefault(),
                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
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

    public async Task<List<OrderResponseDeliveryDto>?> getOrdersBelongToDeliveries(Guid deliveryId, int pagenumber, int pagesize = 25)
    {
        try
        {
            var result = await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.DeleveryId == deliveryId && order.Status > 1
                    select new OrderResponseDeliveryDto
                    {
                        id = order.Id,
                        longitude = order.Longitude,
                        latitude = order.Latitude,
                        userPhone = user.Phone,
                        status = order.Status,
                        deliveryFee = order.DistanceFee,
                        realPrice = _dbContext.OrderItems
                            .Where(oi => oi.OrderId == order.Id && oi.Status == enOrderItemStatus.Excepted)
                            .Sum(oi => oi.Quanity),
                        name = user.Name,
                        totalPrice = order.TotalPrice,
                        orderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new OrderItemResponseDeliveryDto
                            {
                                address = _dbContext.Address
                                    .AsNoTracking()
                                    .Where(st => st.Id == orIt.StoreId)
                                    .Select(ad => new AddressResponseDeliveryDto
                                    {
                                        longitude = ad.Longitude,
                                        latitude = ad.Latitude,
                                    }).FirstOrDefault(),
                                price = orIt.Price,
                                id = orIt.Id,
                                quanity = orIt.Quanity,
                                product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                productVarient = _dbContext.OrdersProductsVarients
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        ProductVarientName = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.varient.Name,
                                    }).ToList(),
                                orderItemStatus = orIt.Status.ToString()
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


    public async Task<bool?> submitOrderToDeliveryId(Guid orderId, Guid deliveryId)
    {
        try
        {
            await _dbContext
                .Orders
                .Where(order => order.Id == orderId)
                .ExecuteUpdateAsync(
                    o => o.SetProperty(value => value.DeleveryId, deliveryId)
                );

            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from update order " + ex.Message);
            return null;
        }
    }


    public async Task<bool?> isOrderCanCencled(Guid orderId)
    {
        try
        {

            var result = await _dbContext.OrderItems
            .Where(oi => oi.OrderId == orderId && oi.Status == enOrderItemStatus.RecivedByDelivery)
            .CountAsync();
            return result > 0;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this excption from savedDistance " + ex.Message);
            return false;
        }
    }

    public async Task<bool?> removeDeliveryFromCurrentOrder(Guid orderId)
    {
        try
        {
            Order? result = await _dbContext
                .Orders
                .FindAsync(orderId);

            if (result == null) return null;

            result.DeleveryId = null;

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