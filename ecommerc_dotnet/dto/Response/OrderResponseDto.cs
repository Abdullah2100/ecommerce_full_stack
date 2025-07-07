namespace ecommerc_dotnet.dto.Response;

public class OrderResponseDto
{
    public Guid id { get; set; }
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    public decimal totalPrice { get; set; }
    public String name { get; set; }
    public String userPhone { get; set; }
    public int status { get; set; }
    public List<OrderItemResponseDto> orderItems { get; set; }
}