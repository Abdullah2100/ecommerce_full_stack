using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class UserInfoDto
{
    public UserInfoDto(
        Guid id, 
        string name, 
        string phone, 
        string email, 
        string? thumbnail=null,
        List<AddressResponseDto>? address=null 
        
       )
    {
        Id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.thumbnail = thumbnail;
        this.address = address;
        
    }

    public Guid Id { get; set; }
     public string name { get; set; }

     public string phone { get; set; }
     public string email { get; set; }
     public string thumbnail { get; set; }
     public List<AddressResponseDto>? address { get; set; }
}