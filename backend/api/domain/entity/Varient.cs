using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.core.entity;

public class Varient
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    public ICollection<ProductVarient> ProductVarients { get; set; }
    
}