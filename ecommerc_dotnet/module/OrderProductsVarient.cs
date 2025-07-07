using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class OrderProductsVarient
{
    [Key]
    public Guid id { get; set; }

    public Guid  productVarientId { get; set; }
    public Guid orderItemId { get; set; }
    
    public ProductVarient productVarient { get; set; }
    public OrderItem orderItem { get; set; }
    
}