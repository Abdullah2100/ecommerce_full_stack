using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public enum enOrderItemStatus {InProgress,Excepted,RecivedByDelivery,Cancelled }

public class OrderItem
{
    [Key]
    public Guid id { get; set; }

    public Guid orderId { get; set; }
    public Guid productId { get; set; }
    public decimal price { get; set; }
    public int quanity { get; set; }
    public Guid storeId { get; set; }

    public Order  order { get; set; }
    public Store store { get; set; }
    
    public ICollection<OrderProductsVarient>orderProductsVarients { get; set; }
    public enOrderItemStatus Status { get; set; }= enOrderItemStatus.InProgress;
}