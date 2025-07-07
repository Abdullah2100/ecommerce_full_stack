namespace ecommerc_dotnet.dto.Response;

public class BannerResponseDto
{
    public Guid id { get; set; }
    
    public String image { get; set; }
    
    public DateTime  endAt{ get; set; }
    public DateTime createdAt { get; set; }

    public Guid storeId { get; set; }
}