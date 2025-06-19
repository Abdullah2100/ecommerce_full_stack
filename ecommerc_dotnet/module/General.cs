using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class General
{
    [Key]
    public Guid id { get; set; }
    
    public string name { get; set; }
    public decimal value { get; set; }
    public DateTime created_at { get; set; } = DateTime.Now;
    [Column(TypeName = "Timestamp")]
    public DateTime? updated_at { get; set; } = null;


}