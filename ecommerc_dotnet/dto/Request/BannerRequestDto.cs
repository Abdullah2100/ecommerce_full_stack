using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Response;

public class BannerRequestDto
{
  [Required]  public IFormFile image { get; set; }
  [Required]  public DateTime  end_at{ get; set; }
}