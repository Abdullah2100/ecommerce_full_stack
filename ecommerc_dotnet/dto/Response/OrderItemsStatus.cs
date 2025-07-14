namespace ecommerc_dotnet.dto.Response;

public class OrderItemsStatus
{
    public Guid orderId { get; set; }
    public Guid orderItemId { get; set; }
    public string status { get; set; }
}