namespace ecommerc_dotnet.dto.Response;

public class OrderProductResponseDto
{
    public Guid id { get; set; }
    public Guid store_id { get; set; }
    public string name { get; set; }
    public string  thmbnail { get; set; }
}