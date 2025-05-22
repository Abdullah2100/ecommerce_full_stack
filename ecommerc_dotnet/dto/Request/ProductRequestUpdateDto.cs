using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class ProductRequestUpdateDto 
{
    [Required]public Guid id { get; set; }
    public string? name { get; set; }
    public string? description { get; set; }
    public IFormFile?  thmbnail { get; set; }
    public Guid? subcategory_id { get; set; }
    [Required] public Guid store_id { get; set; }
    public decimal? price { get; set; }
    public List<ProductVarientRequestDto>? productVarients { get; set; }
    public List<string>? deletedProductVarients { get; set; }
    public List<IFormFile>? images { get; set; }
    public List<string>? deletedimages { get; set; }
}