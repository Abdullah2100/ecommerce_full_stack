namespace ecommerc_dotnet.dto.Request;

public class OrderRequestDto
{
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    public decimal totalPrice { get; set; }
    public List<OrderRequestItemsDto> items { get; set; }
}