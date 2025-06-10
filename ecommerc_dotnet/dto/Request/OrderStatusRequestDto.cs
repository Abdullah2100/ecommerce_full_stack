namespace ecommerc_dotnet.dto.Request;

public class OrderStatusRequestDto
{
    public Guid id { get; set; }
    public int status { get; set; }
}