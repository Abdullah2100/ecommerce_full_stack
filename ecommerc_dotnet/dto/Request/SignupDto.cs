using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Request;

public class SignupDto
{
    [Required] [MaxLength(50)] public string name { get; set; }

    [Required] [MaxLength(13)] public string phone { get; set; }
    [Required] public string email { get; set; }
    [Required] public string password { get; set; }
     public int? role { get; set; } = 1;
}