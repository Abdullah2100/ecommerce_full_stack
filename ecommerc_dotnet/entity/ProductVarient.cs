namespace ecommerc_dotnet.module;

public class ProductVarient
{
    public Guid Id { get; set; }
    public string? Name { get; set; }
    public decimal Precentage { get; set; }
    public Guid VarientId { get; set; }
    public Guid ProductId { get; set; }
    public Varient varient { get; set; }
    public Product product { get; set; }
    
    public ICollection<OrderProductsVarient> orderProductsVarients { get; set; }
    
}