using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.core.entity;

namespace ecommerc_dotnet.domain.entity;

public class ProductImage
{
    [Key]
    public Guid? Id { get; set; }
    public string Path { get; set; }
    public Guid ProductId { get; set; }
    
    public Product  Product { get; set; }
}