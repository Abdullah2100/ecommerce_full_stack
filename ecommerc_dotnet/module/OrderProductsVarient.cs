using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class OrderProductsVarient
{
    [Key]
    public Guid id { get; set; }

    public Guid  product_varient_id { get; set; }
    public Guid order_item_id { get; set; }
    
    public ProductVarient productVarient { get; set; }
    public OrderItem orderItem { get; set; }
    
}