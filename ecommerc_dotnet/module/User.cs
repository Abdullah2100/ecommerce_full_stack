using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class User
{
    [Key]
    public Guid ID { get; set; }
    public string username { get; set; }
    public string  password { get; set; }
    public int role { get; set; }
    [Timestamp]
    public DateTime created_at { get; set; }
    
    [Timestamp]
    public DateTime? updated_at { get; set; }
    public Person person { get; set; }
}