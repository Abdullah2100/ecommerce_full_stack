using ecommerc_dotnet.dto.Request;

namespace ecommerc_dotnet.dto.Response;


public class OrderItemResponseDto
{
    public Guid id { get; set; }
    public Guid orderId { get; set; }
    public decimal price { get; set; }
    public int quanity { get; set; }
    public OrderProductResponseDto? product { get; set; }
    public List<OrderVarientResponseDto> productVarient { get; set; }
    public String orderItemStatus { get; set; } = "";
}
