using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Request;

public class LoginDto
{
    [Required] public string username { get; set; }
    [Required] public string password { get; set; }
}