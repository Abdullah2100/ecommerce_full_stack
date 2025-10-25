using System.ComponentModel.DataAnnotations;

namespace api.domain.entity;

public class OrderProductsVarient
{
    [Key]
    public Guid Id { get; set; }

    public Guid  ProductVariantId { get; set; }
    public Guid OrderItemId { get; set; }
    public Guid Name { get; set; }
    public ProductVariant? ProductVariant { get; set; } = null;
    public OrderItem? OrderItem { get; set; } = null;

}