using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class UserDeliveryInfoResponseDto
{

    public string name { get; set; } 
     public string phone { get; set; }
     public string email { get; set; }
     public string thumbnail { get; set; }
}