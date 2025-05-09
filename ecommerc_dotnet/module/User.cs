using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class User
{
    [Key] public Guid ID { get; set; }
    public string name { get; set; }
    public string phone { get; set; }
    public string  email{ get; set; }
    public string password { get; set; }
    public bool isDeleted { get; set; }
    
    //0 :normal user ; 1: is admin
    public int role { get; set; }
    
    
    [Column(TypeName = "Timestamp")]
     public DateTime created_at { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
     public DateTime? updated_at { get; set; } = null;
    
    public string? thumbnail { get; set; }
    public ICollection<Address>? addresses { get; set; }
}