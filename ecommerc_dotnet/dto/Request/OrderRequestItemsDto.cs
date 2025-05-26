namespace ecommerc_dotnet.dto.Request;

public class OrderRequestItemsDto
{
    public Guid store_id { get; set; }
    public decimal price { get; set; }
    public int quanity { get; set; } = 1;
    public Guid product_Id { get; set; }
    public List<Guid>  products_varient_id { get; set; }
}