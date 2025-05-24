using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class ProductRequestDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not be empty")]
    public string name { get; set; }
    [StringNotNullOrEmptyValidation(ErrorMessage= "description must not be empty")]
    public string description { get; set; }
    public IFormFile  thmbnail { get; set; }
    public Guid subcategory_id { get; set; }
    public Guid store_id { get; set; }
    public decimal price { get; set; }
    public List<ProductVarientRequestDto>? productVarients { get; set; }
    public List<IFormFile> images { get; set; }
}