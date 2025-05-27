using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class AddressRequestDto
{
   [Required] public decimal longitude { get; set; }
   [Required] public decimal latitude { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "address title must not be empty")]
    public string title { get; set; } 
}