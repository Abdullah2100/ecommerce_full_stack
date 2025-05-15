using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class StoreRequestDto
{
  [StringNotNullOrEmptyValidation(ErrorMessage= "Name must not be empty")]
  [Required]  public string name { get; set; }
  [Required]  public IFormFile wallpaper_image { get; set; }
  [Required]  public IFormFile small_image  { get; set; }
  public   Guid? user_id    { get; set; }=null;
  [Required] public decimal longitude  { get; set; }
  [Required] public decimal latitude    { get; set; }
}