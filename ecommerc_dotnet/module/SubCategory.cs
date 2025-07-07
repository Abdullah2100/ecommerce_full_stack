using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class SubCategory
{
    public Guid id { get; set; }
    public string name { get; set; }

    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? updatedAt { get; set; } = null;
    
    public Guid storeId { get; set; }
    public Store? Store { get; set; }
    
    public Guid categoriId { get; set; }
    public Category? category { get; set; } 
    
    public ICollection<Product>? products { get; set; }
}