using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Request;

public class SignupDto
{
    [Required] [MaxLength(50)] public string name { get; set; }

    [Required] [MaxLength(13)] public string phone { get; set; }
    public string? address { get; set; } = "";
    [Required] public string email { get; set; }
    [Required] [MaxLength(50)] public string username { get; set; }
    [Required] public string password { get; set; }
    [Required] public int? role { get; set; } = 1;
}