using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Banner
{
    [Key]
    public Guid Id { get; set; }
    public String Image { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime  EndAt{ get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; }
  
    public Guid StoreId { get; set; }
    public Store Store { get; set; }
}