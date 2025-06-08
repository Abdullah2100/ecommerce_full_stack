using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Response;

public class CategoryRequestUpdatteDto
{
    [Required]public Guid id { get; set; }
    public string? name { get; set; } = null;
    public IFormFile? image { get; set; } = null;
}