using System.ComponentModel.DataAnnotations.Schema;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.core.entity;

public class SubCategory
{
    public Guid Id { get; set; }
    public string Name { get; set; }

    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;
    
    public Guid StoreId { get; set; }
    public Store? Store { get; set; }
    
    public Guid CategoryId { get; set; }
    public Category? Category { get; set; } 
    
    public ICollection<Product>? Products { get; set; }
}