using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class ProductImage
{
    [Key]
    public Guid? ID { get; set; }
    public string name { get; set; }
    public Guid productId { get; set; }
}