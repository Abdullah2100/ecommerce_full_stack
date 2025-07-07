using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class StoreRequestDto
{
  [StringNotNullOrEmptyValidation(ErrorMessage= "Name must not be empty")]
  [Required]  public string name { get; set; }
  [Required]  public IFormFile wallpaperImage { get; set; }
  [Required]  public IFormFile smallImage  { get; set; }
  public   Guid? userId    { get; set; }=null;
  [Required] public decimal longitude  { get; set; }
  [Required] public decimal latitude    { get; set; }
}