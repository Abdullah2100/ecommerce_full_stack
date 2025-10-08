using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.core.entity;

public class Category
{
    [Key]
    public Guid Id { get; set; }

    public string Name { get; set; } 
    public Guid  OwnerId { get; set; }
    public bool IsBlocked { get; set; }=false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;
    
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;

    public string Image { get; set; }
    public User User { get; set; }
    public ICollection<SubCategory>? SubCategories {get;set;}
}