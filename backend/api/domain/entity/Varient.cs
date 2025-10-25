namespace api.domain.entity;

public class Varient
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    public ICollection<ProductVariant> ProductVarients { get; set; }
    
}