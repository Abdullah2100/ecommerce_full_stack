using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class ReseatePasswordOtp
{
    public Guid id { get; set; }
    public string email { get; set; }
    public string otp { get; set; }
    public int validation { get; set; }
    
    
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; }
}