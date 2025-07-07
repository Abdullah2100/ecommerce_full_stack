using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class GeneralSettings
{
    [Key]
    public Guid id { get; set; }
    
    public string name { get; set; }
    public decimal value { get; set; }
    public DateTime createdAt { get; set; } = DateTime.Now;
    [Column(TypeName = "Timestamp")]
    public DateTime? updatedAt { get; set; } = null;


}