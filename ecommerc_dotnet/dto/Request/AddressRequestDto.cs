using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class AddressRequestDto
{
    public Guid? id { get; set; } = null;
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    
    [StringNotNullOrEmptyValidation(ErrorMessage= "address title must not be empty")]
    public string title { get; set; } 
}