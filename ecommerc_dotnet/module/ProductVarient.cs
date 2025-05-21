namespace ecommerc_dotnet.module;

public class ProductVarient
{
    public Guid id { get; set; }
    public string? name { get; set; }
    public decimal precentage { get; set; }
    public Guid varient_id { get; set; }
    public Guid product_id { get; set; }
    public Varient varient { get; set; }
    public Product product { get; set; }
    
}