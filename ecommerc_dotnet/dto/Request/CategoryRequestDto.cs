using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Response;

public class CategoryRequestDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "Name must not be empty")]
    [Required] public string name { get; set; }
    [Required] public IFormFile image { get; set; }
    
}