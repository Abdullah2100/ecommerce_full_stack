namespace ecommerc_dotnet.module;

public class Varient
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    public ICollection<ProductVarient> ProductVarients { get; set; }
    
}