using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Delivery 
{
    [Key] public Guid ID { get; set; }
     public Guid user_Id { get; set; }
    public string deviceToken { get; set; }
    public bool isAvaliable { get; set; } = true;

    
    [Column(TypeName = "Timestamp")]
     public DateTime created_at { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
     public DateTime? updated_at { get; set; } = null;
    
    public string? thumbnail { get; set; }
    public Address? address { get; set; }
    public User user { get; set; }
   
    public ICollection<Order>? orders { get; set; }
}