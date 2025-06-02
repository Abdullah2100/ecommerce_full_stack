using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class SignupDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "Name must not be empty")]

    [Required] [MaxLength(50)] public string name { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "phone must not be empty")]
    [Required] [MaxLength(13)] public string phone { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "phone must not be empty")]
    [Required] public string email { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "password must not be empty")]
    [Required] public string password { get; set; }
    public string? deviceToken { get; set; } = null;
     public int? role { get; set; } = 1;
}