using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.domain.entity;

public enum enOrderItemStatus {Cancelled,InProgress,Excepted,ReceivedByDelivery,Delivered }

public class OrderItem
{
    [Key]
    public Guid Id { get; set; }

    public Guid OrderId { get; set; }
    public Guid ProductId { get; set; }
    public decimal Price { get; set; }
    public int Quanity { get; set; }
    public Guid StoreId { get; set; }

    public Order  Order { get; set; }
    public Store Store { get; set; }
    public Product Product { get; set; }
    public ICollection<OrderProductsVarient>? OrderProductsVarients { get; set; } = null;
    public enOrderItemStatus Status { get; set; }= enOrderItemStatus.InProgress;
}