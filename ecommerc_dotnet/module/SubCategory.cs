using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class SubCategory
{
    public Guid id { get; set; }
    public string name { get; set; }

    [Column(TypeName = "Timestamp")]
    public DateTime created_at { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? updated_at { get; set; } = null;
    
    public Guid store_id { get; set; }
    public Store? Store { get; set; }
    
    public Guid categori_id { get; set; }
    public Category? category { get; set; } 
}