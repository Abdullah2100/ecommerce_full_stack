using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Category
{
    [Key]
    public Guid id { get; set; }
    public string name { get; set; }
    public Guid  ownerId { get; set; }
    public bool isBlocked { get; set; }=false;
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; } = DateTime.Now;
    [Column(TypeName = "Timestamp")]
    public DateTime? updatedAt { get; set; } = null;

    public string image { get; set; }
    public User user { get; set; }
    public ICollection<SubCategory>? subCategories {get;set;}
}