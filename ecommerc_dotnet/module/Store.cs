using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Store
{
    [Key]
    public Guid id { get; set; }
    public string name { get; set; }
    public string wallpaperImage  { get; set; }
    public string  smallImage { get; set; }
    public bool isBlock { get; set; } = true;

    public Guid userId { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? updatedAt { get; set; } = null;
    
    public ICollection<Address>? addresses { get; set; } =null;
    public ICollection<SubCategory>? SubCategories { get; set; } =null;
    public ICollection<Bannel>? banners { get; set; }
    public ICollection<Product>? products { get; set; }
    public ICollection<OrderItem>oddrderItems{ get; set; }

    public User user {get; set;}
}