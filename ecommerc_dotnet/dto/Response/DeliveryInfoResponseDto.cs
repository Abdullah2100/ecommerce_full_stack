namespace ecommerc_dotnet.dto.Response;

public class DeliveryInfoResponseDto
{
    public Guid id { get; set; }
    public Guid userId { get; set; }
    public bool isAvaliable { get; set; } = true;
    public DateTime createdAt { get; set; } = DateTime.Now;
    public string? thumbnail { get; set; }
    public AddressResponseDto address { get; set; }
    public DeliveryAnalysDto? analys { get; set; } = null;

}