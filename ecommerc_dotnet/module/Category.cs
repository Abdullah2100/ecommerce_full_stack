using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Category
{
    [Key]
    public Guid id { get; set; }
    public string name { get; set; }
    public Guid  owner_id { get; set; }
    public bool isBlocked { get; set; }=false;
    [Column(TypeName = "Timestamp")]
    public DateTime created_at { get; set; } = DateTime.Now;
    [Column(TypeName = "Timestamp")]
    public DateTime? updated_at { get; set; } = null;

    public string image_path { get; set; }
    public User user { get; set; }
}