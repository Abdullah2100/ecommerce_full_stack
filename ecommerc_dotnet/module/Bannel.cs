using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Bannel
{
    [Key]
    public Guid id { get; set; }
    
    public String image { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime  endAt{ get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; }

    public Guid storeId { get; set; }
    public Store store { get; set; }
}