using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Bannel
{
    [Key]
    public Guid id { get; set; }
    
    public String image { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime  end_at{ get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime create_at { get; set; }

    public Guid store_id { get; set; }
    public Store store { get; set; }
}