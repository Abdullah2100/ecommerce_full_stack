namespace ecommerc_dotnet.module;

public class ProductVarient
{
    public Guid Id { get; set; }
    public decimal Precentage { get; set; }
    public Guid VarientId { get; set; }
    public string Name { get; set; }
    public Guid ProductId { get; set; }
    public Varient Varient { get; set; }
    public Product Product { get; set; }
    
    public ICollection<OrderProductsVarient> OrderProductsVarients { get; set; }
    
}