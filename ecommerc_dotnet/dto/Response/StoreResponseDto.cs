namespace ecommerc_dotnet.dto.Response;

public class StoreResponseDto
{
   
    public Guid id { get; set; }
    public string name { get; set; }
    public string wallpaper_image  { get; set; }
    public string  small_image { get; set; }
    public string userName { get; set; } = "";
    public bool  isBlocked { get; set; }
    
    public Guid user_id { get; set; }
    public DateTime created_at { get; set; } = DateTime.Now;
}
