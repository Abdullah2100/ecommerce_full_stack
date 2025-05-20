using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Response;

public class SubCategoryRquestDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not  be empty")]
 [Required]   public string name { get; set; }
 [Required]   public Guid cateogy_id { get; set; } 
}