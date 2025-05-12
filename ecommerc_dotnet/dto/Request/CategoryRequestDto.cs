using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Response;

public class CategoryRequestDto
{
    [Required] public string name { get; set; }
    [Required] public IFormFile image { get; set; }
}