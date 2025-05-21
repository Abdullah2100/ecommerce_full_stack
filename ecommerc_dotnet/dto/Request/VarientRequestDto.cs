using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request;

public class VarientRequestDto
{
    public Guid? id { get; set; } = null;
 [Required]   public string name { get; set; }
}