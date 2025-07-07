using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class ProductResponseDto
{
    public Guid id { get; set; }
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not be empty")]
    public string name { get; set; }
    [StringNotNullOrEmptyValidation(ErrorMessage= "description must not be empty")]
    public string description { get; set; }
    public string  thmbnail { get; set; }
    public Guid subcategoryId { get; set; }
    public Guid categoryId { get; set; }
    public Guid storeId { get; set; }
    public decimal price { get; set; }
    public List<List<ProductVarientResponseDto>>? productVarients { get; set; }
    public List<string> productImages { get; set; }
}