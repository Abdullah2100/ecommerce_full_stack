using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Store
{
    [Key]
    public Guid id { get; set; }
    public string name { get; set; }
    public string wallpaper_image  { get; set; }
    public string  small_image { get; set; }
    public bool isBlock { get; set; } = true;

    public Guid user_id { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime created_at { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? updated_at { get; set; } = null;
    
    public ICollection<Address>? addresses { get; set; } =null;
    public ICollection<SubCategory>? SubCategories { get; set; } =null;
    public ICollection<Bannel>? banners { get; set; }

    public User user {get; set;}
}