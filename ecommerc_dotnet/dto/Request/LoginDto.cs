using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class LoginDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "username must not be empty")]
    [Required] public string username { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "password must not be empty")]
    [Required] public string password { get; set; }
}