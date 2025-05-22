namespace ecommerc_dotnet.dto.Request;

public class ProductVarientResponseDto
{
    public Guid id { get; set; }
    public string? name { get; set; }
    public decimal precentage { get; set; }
    public Guid varient_id { get; set; }
}