namespace ecommerc_dotnet.dto.Response;

public class OrderProductDto
{
    public Guid Id { get; set; }
    public Guid StoreId { get; set; }
    public string Name { get; set; }
    public string  Thmbnail { get; set; }
}