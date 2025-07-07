using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class ProductImage
{
    [Key]
    public Guid? id { get; set; }
    public string name { get; set; }
    public Guid productId { get; set; }
    
    public Product  product { get; set; }
}