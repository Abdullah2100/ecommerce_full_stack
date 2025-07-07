namespace ecommerc_dotnet.dto.Response;

public class StoreResponseDto
{
   
    public Guid id { get; set; }
    public string name { get; set; }
    public string wallpaperImage  { get; set; }
    public string  smallImage { get; set; }
    public string userName { get; set; } = "";
    public bool  isBlocked { get; set; }
    public decimal? longitude { get; set; } = null;
    public decimal? latitude { get; set; } = null;
    
    public Guid userId { get; set; }
    public DateTime createdAt { get; set; } = DateTime.Now;
}
