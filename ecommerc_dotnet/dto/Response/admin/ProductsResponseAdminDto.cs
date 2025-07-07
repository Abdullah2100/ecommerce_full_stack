using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Response;

public class ProductsResponseAdminDto
{
    public Guid id { get; set; }
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not be empty")]
    public string name { get; set; }
    [StringNotNullOrEmptyValidation(ErrorMessage= "description must not be empty")]
    public string description { get; set; }
    public string  thmbnail { get; set; }
    public string subcategory { get; set; }
    public string store { get; set; }
    public decimal price { get; set; }
    public List<List<ProductVarientResonseAdminDto>>? productVarients { get; set; }
    public List<string> productImages { get; set; } 
}