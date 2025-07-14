namespace ecommerc_dotnet.dto.Response;

public class DeliveryAnalysDto
{
    public decimal? dayFee { get; set; } = null;
    public decimal? weekFee { get; set; } = null;
    public decimal? monthFee { get; set; } = null;
    public int? dayOrder { get; set; } = null;
    public int? weekOrder { get; set; } = null;
}