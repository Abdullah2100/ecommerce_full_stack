using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class OrderItem
{
    [Key]
    public Guid id { get; set; }

    public Guid order_id { get; set; }
    public Guid product_id { get; set; }
    public decimal price { get; set; }
    public int quanity { get; set; }
    public Guid store_id { get; set; }

    public Order  order { get; set; }
    public Store Store { get; set; }
    
    public ICollection<OrderProductsVarient>orderProductsVarients { get; set; }
}