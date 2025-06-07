using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class UserInfoResponseDto
{

    public Guid Id { get; set; }
     public string name { get; set; }

     public bool isAdmin { get; set; } = false;
     public string phone { get; set; }
     public string email { get; set; }
     public string storeName { get; set; } = "";
     public bool isActive { get; set; } = true;
     public string thumbnail { get; set; }
     public List<AddressResponseDto>? address { get; set; }
     public Guid? store_id { get; set; }
}