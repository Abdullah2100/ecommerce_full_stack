using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Delivery 
{
    [Key] public Guid id { get; set; }
     public Guid userId { get; set; }
    public string deviceToken { get; set; }
    public bool isAvaliable { get; set; } = true;

    
    [Column(TypeName = "Timestamp")]
     public DateTime createdAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
     public DateTime? updatedAt { get; set; } = null;
    
    public string? thumbnail { get; set; }
    public Address? address { get; set; }
    public User user { get; set; }
   
    public ICollection<Order>? orders { get; set; }
}