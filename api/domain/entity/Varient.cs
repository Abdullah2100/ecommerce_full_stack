namespace api.domain.entity;

public class Variant
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    public ICollection<ProductVariant> ProductVariants { get; set; }
    
}