using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
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
    private readonly IUnitOfWork _unitOfWork;

    public OrderData(
        AppDbContext dbContext,
        IConfig config,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _config = config;
        _unitOfWork = unitOfWork;
    }


    public async Task<List<OrderDto>?> getOrder(int pagenumber, int pagesize = 25)
    {
        return await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.UserId equals user.Id
                select new OrderDto
                {
                    Id = order.Id,
                    Longitude = order.Longitude,
                    Latitude = order.Latitude,
                    UserPhone = user.Phone,
                    Status = order.Status,
                    DeliveryFee = order.DistanceFee,

                    Name = user.Name,
                    TotalPrice = order.TotalPrice,
                    OrderItems = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.OrderId == order.Id)
                        .Select(orIt => new OrderItemDto
                        {
                            Price = orIt.Price,
                            Id = orIt.Id,
                            Quanity = orIt.Quanity,
                            Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                new OrderProductDto
                                {
                                    Id = pr.Id,
                                    Name = pr.Name,
                                    Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                }).FirstOrDefault(),
                            ProductVarient = _dbContext.OrdersProductsVarients
                                .Include(po => po.ProductVarient)
                                .AsNoTracking()
                                .Where(opv => opv.OrderItemId == orIt.Id)
                                .Select(opv => new OrderVarientDto
                                {
                                    Name = opv.ProductVarient.Name,
                                    VarientName = opv.ProductVarient.Varient.Name,
                                }).ToList(),
                        }).ToList()
                }
            )
            .AsNoTracking()
            .Skip((pagenumber - 1) * pagesize)
            .Take(pagesize)
            .ToListAsync();
    }

    public async Task<List<OrderItemDto>?> getOrderItems
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
                .Select(oi => new OrderItemDto
                {
                    Id = oi.Id,
                    Quanity = oi.Quanity,
                    OrderId = oi.OrderId,
                    Product = _dbContext.Products.Where(ois => ois.Id == oi.ProductId)
                        .Select(p => new OrderProductDto
                        {
                            Id = p.Id,
                            Name = p.Name,
                            Thmbnail = _config.getKey("url_file") + p.Thmbnail,
                        }).FirstOrDefault(),
                    ProductVarient = _dbContext.OrdersProductsVarients
                        .Include(p => p.ProductVarient)
                        .AsNoTracking()
                        .Where(opv => opv.OrderItemId == oi.Id)
                        .Select(opv => new OrderVarientDto
                        {
                            Name = opv.ProductVarient!.Name,
                            VarientName = opv.ProductVarient!.Varient.Name,
                        }).ToList(),
                    OrderItemStatus = oi.Status.ToString()
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

    public async Task<OrderItemDto?> getOrderItem
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
                .Select(oi => new OrderItemDto
                {
                    Id = oi.Id,
                    Quanity = oi.Quanity,
                    OrderId = oi.OrderId,
                    Product = _dbContext.Products.Where(ois => ois.Id == oi.ProductId)
                        .Select(p => new OrderProductDto
                        {
                            Id = p.Id,
                            Name = p.Name,
                            Thmbnail = _config.getKey("url_file") + p.Thmbnail,
                        }).FirstOrDefault(),
                    ProductVarient = _dbContext.OrdersProductsVarients
                        .AsNoTracking()
                        .Where(opv => opv.OrderItemId == oi.Id)
                        .Select(opv => new OrderVarientDto
                        {
                            Name = opv.ProductVarient!.Name,
                            VarientName = opv.ProductVarient.Varient.Name,
                        }).ToList(),
                    OrderItemStatus = oi.Status.ToString()
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

    public async Task<List<OrderDto>?> getOrder
    (
        Guid userid,
        int pagenumber,
        int pagesize = 25
    )
    {
        return await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.UserId equals user.Id
                where user.Id == userid
                select new OrderDto
                {
                    Id = order.Id,
                    Longitude = order.Longitude,
                    Latitude = order.Latitude,
                    UserPhone = user.Phone,
                    Status = order.Status,
                    DeliveryFee = order.DistanceFee,
                    TotalPrice = order.TotalPrice,
                    OrderItems = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.OrderId == order.Id)
                        .Select(orIt => new OrderItemDto
                        {
                            OrderItemStatus = orIt.Status.ToString(),
                            Price = orIt.Price,
                            Id = orIt.Id,
                            Quanity = orIt.Quanity,
                            Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                new OrderProductDto
                                {
                                    Id = pr.Id,
                                    Name = pr.Name,
                                    Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                }).FirstOrDefault(),
                            ProductVarient = _dbContext.OrdersProductsVarients
                                .Include(p => p.ProductVarient)
                                .AsNoTracking()
                                .Where(opv => opv.OrderItemId == orIt.Id)
                                .Select(opv => new OrderVarientDto
                                {
                                    Name = opv.ProductVarient.Name,
                                    VarientName = opv.ProductVarient.Varient.Name,
                                }).ToList(),
                        }).ToList()
                }
            )
            .AsNoTracking()
            .Skip((pagenumber - 1) * pagesize)
            .Take(pagesize)
            .ToListAsync();
    }

    public async Task<bool> updateOrderItemStatus(Guid id, enOrderItemStatusDto status)
    {
        await _dbContext.OrderItems
            .Where(oi => oi.Id == id)
            .ExecuteUpdateAsync(
                oi => oi
                    .SetProperty(value => value.Status,
                        status == enOrderItemStatusDto.Excepted
                            ? enOrderItemStatus.Excepted
                            : enOrderItemStatus.Cancelled
                    )
            );

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }


    public async Task<bool> deleteOrder(Guid userid, Guid orderId)
    {
        await _dbContext
            .Orders
            .Where(or => or.UserId == userid && or.Id == orderId)
            .ExecuteDeleteAsync();

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }

    public async Task<bool?> isExist(Guid id)
    {
        return await _dbContext.Orders.FindAsync(id) != null;
    }

    public async Task<Order?> getOrderById(Guid id)
    {
        Order? order = await _dbContext.Orders.FindAsync(id);

        return order;
    }


    public async Task<OrderDto?> getOrder(Guid id)
    {
        return await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.UserId equals user.Id
                where order.Id == id
                select new OrderDto
                {
                    Id = order.Id,
                    Longitude = order.Longitude,
                    Latitude = order.Latitude,
                    UserPhone = user.Phone,
                    Status = order.Status,
                    Name = user.Name,
                    DeliveryFee = order.DistanceFee,
                    OrderItems = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.OrderId == order.Id)
                        .Select(orIt => new OrderItemDto
                        {
                            Price = orIt.Price,
                            Id = orIt.Id,
                            Quanity = orIt.Quanity,
                            Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                new OrderProductDto
                                {
                                    StoreId = pr.StoreId,
                                    Id = pr.Id,
                                    Name = pr.Name,
                                    Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                }).FirstOrDefault(),
                            ProductVarient = _dbContext.OrdersProductsVarients
                                .Include(p => p.ProductVarient)
                                .AsNoTracking()
                                .Where(opv => opv.OrderItemId == orIt.Id)
                                .Select(opv => new OrderVarientDto
                                {
                                    Name = opv.ProductVarient.Name,
                                    VarientName = opv.ProductVarient.Varient.Name,
                                }).ToList(),
                        }).ToList()
                }
            ).AsNoTracking()
            .FirstOrDefaultAsync();
    }

    public async Task<OrderDto?> getOrder(Guid id, Guid userid)
    {
        return await (
                from order in _dbContext.Orders
                join user in _dbContext.Users on order.UserId equals user.Id
                where order.Id == id && user.Id == userid
                select new OrderDto
                {
                    Id = order.Id,
                    Longitude = order.Longitude,
                    Latitude = order.Latitude,
                    UserPhone = user.Phone,
                    Status = order.Status,
                    DeliveryFee = order.DistanceFee,
                    Name = user.Name,
                    OrderItems = _dbContext.OrderItems
                        .AsNoTracking()
                        .Where(oi => oi.OrderId == order.Id)
                        .Select(orIt => new OrderItemDto
                        {
                            Price = orIt.Price,
                            Id = orIt.Id,
                            Quanity = orIt.Quanity,
                            Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                new OrderProductDto
                                {
                                    StoreId = pr.StoreId,
                                    Id = pr.Id,
                                    Name = pr.Name,
                                    Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                }).FirstOrDefault(),
                            ProductVarient = _dbContext.OrdersProductsVarients
                                .Include(p => p.ProductVarient)
                                .AsNoTracking()
                                .Where(opv => opv.OrderItemId == orIt.Id)
                                .Select(opv => new OrderVarientDto
                                {
                                    Name = opv.ProductVarient.Name,
                                    VarientName = opv.ProductVarient.Varient.Name,
                                }).ToList(),
                        }).ToList()
                }
            ).AsNoTracking()
            .FirstOrDefaultAsync();
    }


    public async Task<int> getOrdersSize()
    {
        var result = await _dbContext
            .Orders
            .AsNoTracking()
            .CountAsync();
        return (int)Math.Ceiling((double)result / 25);
    }

    private async Task<bool> isSavedDistance(Guid orderId)
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


    public async Task<OrderDto?> createOrder
    (
        Guid userId,
        decimal longitude,
        decimal latitude,
        decimal totalPrice,
        List<CreateOrderItemDto> items
    )
    {
        var id = clsUtil.generateGuid();
        await _unitOfWork.OrderRepository.addAsync(new Order
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
            _unitOfWork.OrderItemRepository.addAsync(new OrderItem
            {
                Id = orderItemId,
                OrderId = id,
                ProductId = item.ProductId,
                Quanity = item.Quantity,
                StoreId = item.StoreId,
                Price = item.Price,
            });
            item?.ProductsVarientId?.ForEach(pv =>
            {
                _unitOfWork.OrderProductsVarientRepository.addAsync(
                    new OrderProductsVarient
                    {
                        Id = clsUtil.generateGuid(),
                        OrderItemId = orderItemId,
                        ProductVarientId = pv
                    }
                );
            });
        });

        int resul = await _unitOfWork.Complate();
        if (resul == 0) return null;
        var isSaved = await isSavedDistance(id);
        if (!isSaved)
        {
            await deleteOrder(userId, id);
            return null;
        }

        return await getOrder(id);
    }

    public async Task<bool> updateOrderStatus(
        Guid orderId,
        int status
        )
    {
        await _dbContext
            .Orders
            .Where(o => o.Id == orderId)
            .ExecuteUpdateAsync(
                o => o.SetProperty(value => value.Status, status)
            );

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }


    //delivery
    public async Task<List<DeliveryOrderDto>?> getOrdersNotBelongToDeliveries(
        int pagenumber,
        int pagesize = 25
        )
    {
       return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.DeleveryId == null && order.Status > 1
                    select new DeliveryOrderDto() 
                    {
                        Id = order.Id,
                        Longitude = order.Longitude,
                        Latitude = order.Latitude,
                        UserPhone = user.Phone,
                        Status = order.Status,
                        DeliveryFee = order.DistanceFee,
                        RealPrice = _dbContext.OrderItems
                            .Where(oi => oi.OrderId == order.Id && oi.Status == enOrderItemStatus.Excepted)
                            .Sum(oi => oi.Quanity),
                        Name = user.Name,
                        TotalPrice = order.TotalPrice,
                        OrderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new DeliveryOrderItemDto
                            {
                                Address = _dbContext.Address
                                    .AsNoTracking()
                                    .Where(st => st.Id == orIt.StoreId)
                                    .Select(ad => new DeliveryAddressDto 
                                    {
                                        Longitude = ad.Longitude,
                                        Latitude = ad.Latitude,
                                    }).FirstOrDefault(),
                                Price = orIt.Price,
                                Id = orIt.Id,
                                Quanity = orIt.Quanity,
                                Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                ProductVarient = _dbContext.OrdersProductsVarients
                                    .Include(p=>p.ProductVarient)
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        Name = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.Varient.Name,
                                    }).ToList(),
                            }).ToList()
                    }
                )
                .AsNoTracking()
                .Skip((pagenumber - 1) * pagesize)
                .Take(pagesize)
                .ToListAsync();
       
    }

    public async Task<List<DeliveryOrderDto>?> getOrdersBelongToDeliveries(
        Guid deliveryId, 
        int pagenumber,
        int pagesize = 25)
    {
       return await (
                    from order in _dbContext.Orders
                    join user in _dbContext.Users on order.UserId equals user.Id
                    where order.DeleveryId == deliveryId && order.Status > 1
                    select new DeliveryOrderDto 
                    {
                        Id = order.Id,
                        Longitude = order.Longitude,
                        Latitude = order.Latitude,
                        UserPhone = user.Phone,
                        Status = order.Status,
                        DeliveryFee = order.DistanceFee,
                        RealPrice = _dbContext.OrderItems
                            .Where(oi => oi.OrderId == order.Id && oi.Status == enOrderItemStatus.Excepted)
                            .Sum(oi => oi.Quanity),
                        Name = user.Name,
                        TotalPrice = order.TotalPrice,
                        OrderItems = _dbContext.OrderItems
                            .AsNoTracking()
                            .Where(oi => oi.OrderId == order.Id)
                            .Select(orIt => new DeliveryOrderItemDto 
                            {
                                Address = _dbContext.Address
                                    .AsNoTracking()
                                    .Where(st => st.Id == orIt.StoreId)
                                    .Select(ad => new DeliveryAddressDto 
                                    {
                                        Longitude = ad.Longitude,
                                        Latitude = ad.Latitude,
                                    }).FirstOrDefault(),
                                Price = orIt.Price,
                                Id = orIt.Id,
                                Quanity = orIt.Quanity,
                                Product = _dbContext.Products.Where(p => p.Id == orIt.ProductId).Select(pr =>
                                    new OrderProductDto
                                    {
                                        Id = pr.Id,
                                        Name = pr.Name,
                                        Thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                                    }).FirstOrDefault(),
                                ProductVarient = _dbContext.OrdersProductsVarients
                                    .Include(p=>p.ProductVarient)
                                    .AsNoTracking()
                                    .Where(opv => opv.OrderItemId == orIt.Id)
                                    .Select(opv => new OrderVarientDto
                                    {
                                        Name = opv.ProductVarient.Name,
                                        VarientName = opv.ProductVarient.Varient.Name,
                                    }).ToList(),
                                OrderItemStatus = orIt.Status.ToString()
                            }).ToList()
                    }
                )
                .AsNoTracking()
                .Skip((pagenumber - 1) * pagesize)
                .Take(pagesize)
                .ToListAsync();
         
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