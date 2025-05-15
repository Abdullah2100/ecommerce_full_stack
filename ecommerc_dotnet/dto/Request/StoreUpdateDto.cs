using ecommerc_dotnet.Validation;
using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class StoreUpdateDto
{
 
    public string? name { get; set; }
    public IFormFile? wallpaper_image { get; set; } = null;
    public IFormFile? small_image { get; set; } = null;
   public decimal? longitude  { get; set; }
   public decimal? latitude    { get; set; }
}