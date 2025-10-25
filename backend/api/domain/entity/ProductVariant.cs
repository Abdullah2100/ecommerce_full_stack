namespace api.domain.entity;

public class ProductVariant
{
    public Guid Id { get; set; }
    public decimal Precentage { get; set; }
    public Guid VariantId { get; set; }
    public string Name { get; set; }
    public Guid ProductId { get; set; }
    public Varient? Varient { get; set; } = null;
    public Product? Product { get; set; } = null;

    public ICollection<OrderProductsVarient>? OrderProductsVariants { get; set; } = null;

}