using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.domain.entity;

public class ProductVarient
{
    public Guid Id { get; set; }
    public decimal Precentage { get; set; }
    public Guid VarientId { get; set; }
    public string Name { get; set; }
    public Guid ProductId { get; set; }
    public Varient? Varient { get; set; } = null;
    public Product? Product { get; set; } = null;

    public ICollection<OrderProductsVarient>? OrderProductsVarients { get; set; } = null;

}