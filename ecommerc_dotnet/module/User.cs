using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class User
{
    [Key] public Guid id { get; set; }
    public string name { get; set; }
    public string phone { get; set; }
    public string  email{ get; set; }
    public string password { get; set; }
    public bool isDeleted { get; set; } = false;
    public string deviceToken { get; set; }
    
    //1 :normal user ; 0: is admin
    public int role { get; set; } = 1;

    
    [Column(TypeName = "Timestamp")]
     public DateTime createdAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
     public DateTime? updatedAt { get; set; } = null;
    
    public string? thumbnail { get; set; }
    public ICollection<Address>? addresses { get; set; }
    public ICollection<Category>? categories { get; set; }
    public ICollection<Order>? orders { get; set; }

    public Store? store { get; set; } = null;
    public Delivery? delivery { get; set; } = null;
}