using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class UserInfoResponseDto
{

    public Guid Id { get; set; }
     public string name { get; set; }

     public string phone { get; set; }
     public string email { get; set; }
     public string thumbnail { get; set; }
     public List<AddressResponseDto>? address { get; set; }
     public StoreResponseDto? store { get; set; }
}