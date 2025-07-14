namespace ecommerc_dotnet.dto.Request;

public class DeliveryRequestDto
{
    public Guid userId { get; set; }
    public string deviceToken { get; set; }
    public IFormFile? thumbnail { get; set; }
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
}