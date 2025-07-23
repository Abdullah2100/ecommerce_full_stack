using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class OrderProductsVarient
{
    [Key]
    public Guid Id { get; set; }

    public Guid  ProductVarientId { get; set; }
    public Guid OrderItemId { get; set; }
    
    public ProductVarient ProductVarient { get; set; }
    public OrderItem OrderItem { get; set; }
    
}