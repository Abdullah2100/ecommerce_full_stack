using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Product
{
    public Guid id { get; set; }
    public string name { get; set; }
    
    [Column(TypeName = "TEXT")]
    public string description { get; set; }
    public string  thmbnail { get; set; }
    public Guid subcategoryId { get; set; }
    public Guid storeId { get; set; }
    public decimal price { get; set; }
    public int? quanity { get; set; } = null;
    
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; }

    [Column(TypeName = "Timestamp")] 
    public DateTime? updatedAt { get; set; } = null;
    
    public ICollection<ProductVarient> productVarients { get; set; }
    public ICollection<ProductImage> productImages { get; set; }
    
   public SubCategory subCategory { get; set; }
   public Store store { get; set; }
}