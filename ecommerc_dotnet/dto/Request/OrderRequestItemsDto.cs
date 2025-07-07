namespace ecommerc_dotnet.dto.Request;

public class OrderRequestItemsDto
{
    public Guid storeId { get; set; }
    public decimal price { get; set; }
    public int quanity { get; set; } = 1;
    public Guid productId { get; set; }
    public List<Guid>  products_varientId { get; set; }
}