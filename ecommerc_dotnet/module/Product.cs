using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Product
{
    public Guid id { get; set; }
    public string name { get; set; }
    
    [Column(TypeName = "TEXT")]
    public string description { get; set; }
    public string  thmbnail { get; set; }
    public Guid subcategory_id { get; set; }
    public Guid store_id { get; set; }
    public decimal price { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime create_at { get; set; }

    [Column(TypeName = "Timestamp")] 
    public DateTime? update_at { get; set; } = null;
    
    public ICollection<ProductVarient> productVarients { get; set; }
    public ICollection<ProductImage> productImages { get; set; }
    
   public SubCategory subCategory { get; set; }
   public Store store { get; set; }
}