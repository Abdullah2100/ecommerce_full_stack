namespace ecommerc_dotnet.module;

public class ProductVarient
{
    public Guid id { get; set; }
    public string? name { get; set; }
    public decimal precentage { get; set; }
    public Guid varientId { get; set; }
    public Guid productId { get; set; }
    public Varient varient { get; set; }
    public Product product { get; set; }
    
    public ICollection<OrderProductsVarient> orderProductsVarients { get; set; }
    
}