namespace ecommerc_dotnet.dto.Request;

public class AddressRequestDto
{
    public Guid? id { get; set; } = null;
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    public string title { get; set; } 
}