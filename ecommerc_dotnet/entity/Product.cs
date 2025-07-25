using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

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
    
    public ICollection<ProductVarient> ProductVarients { get; set; }
    public ICollection<ProductImage> ProductImages { get; set; }
    
   public SubCategory subCategory { get; set; }
   public Store store { get; set; }
}