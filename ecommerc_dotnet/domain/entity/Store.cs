using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.entity;

namespace ecommerc_dotnet.module;

public class Store
{
    [Key]
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string WallpaperImage  { get; set; }
    public string  SmallImage { get; set; }
    public bool IsBlock { get; set; } = true;

    public Guid UserId { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;
    
    public ICollection<Address>? Addresses { get; set; } =null;
    public ICollection<SubCategory>? SubCategories { get; set; } =null;
    public ICollection<Banner>? Banners { get; set; } = null;
    public ICollection<Product>? Products { get; set; } = null;
    public ICollection<OrderItem>? OddrderItems { get; set; } = null;

    public User user {get; set;}
}