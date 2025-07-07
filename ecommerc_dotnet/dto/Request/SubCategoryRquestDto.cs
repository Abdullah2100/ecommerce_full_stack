using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Response;

public class NewBaseType
{
    [Required] public Guid cateogy_id { get; set; }
}

public class SubCategoryRquestDto : NewBaseType
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not  be empty")]
 [Required]   public string name { get; set; }
}