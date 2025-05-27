using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class AddressRequestUpdateDto
{
    [Required]public Guid id { get; set; } 
    public decimal? longitude { get; set; } = null;
     public decimal? latitude { get; set; } = null;
     public string? title { get; set; } = null;
}