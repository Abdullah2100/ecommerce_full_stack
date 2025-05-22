using ecommerc_dotnet.Validation;

namespace ecommerc_dotnet.dto.Request;

public class ProductVarientRequestDto
{
    [StringNotNullOrEmptyValidation(ErrorMessage= "name must not be empty")]
    public string? name { get; set; }
    public decimal? precentage { get; set; } = 0;
    public Guid varient_id { get; set; }
}