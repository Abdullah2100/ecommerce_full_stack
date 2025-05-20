namespace ecommerc_dotnet.dto.Response;

public class BannerResponseDto
{
    public Guid id { get; set; }
    
    public String image { get; set; }
    
    public DateTime  end_at{ get; set; }
    public DateTime create_at { get; set; }

    public Guid store_id { get; set; }
}