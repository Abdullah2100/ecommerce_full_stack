using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class ProductRequestUpdateDto 
{
    [Required]public Guid id { get; set; }
    public string? name { get; set; }= null;
    public string? description { get; set; }= null;
    public IFormFile?  thmbnail { get; set; }= null;
    public Guid? subcategory_id { get; set; }= null;
    [Required] public Guid store_id { get; set; }
    public decimal? price { get; set; }= null;
    public List<ProductVarientRequestDto>? productVarients { get; set; } = null;
    public List<ProductVarientRequestDto>? deletedProductVarients { get; set; } = null;
    public List<IFormFile>? images { get; set; }= null;
    public List<string>? deletedimages { get; set; }= null;
}