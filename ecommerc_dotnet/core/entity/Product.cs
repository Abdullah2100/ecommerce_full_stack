using System.ComponentModel.DataAnnotations.Schema;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.entity;

public class Product
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    [Column(TypeName = "TEXT")]
    public string Description { get; set; }
    public string  Thmbnail { get; set; }
    public Guid SubcategoryId { get; set; }
    public Guid StoreId { get; set; }
    public decimal Price { get; set; }
    
    public int? Quanity { get; set; } = null;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; }

    [Column(TypeName = "Timestamp")] 
    public DateTime? UpdatedAt { get; set; } = null;

    public ICollection<ProductVarient>? ProductVarients { get; set; } = null;
    public ICollection<ProductImage>? ProductImages { get; set; } = null;
    
   public SubCategory subCategory { get; set; }
   public Store store { get; set; }
}