using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Response;

public class CategoryRequestUpdatteDto
{
    [Required]public Guid id { get; set; }
    public string name { get; set; }
    public IFormFile? image { get; set; }
}