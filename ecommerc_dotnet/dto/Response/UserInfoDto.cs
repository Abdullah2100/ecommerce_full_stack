namespace ecommerc_dotnet.dto.Response;

public class UserInfoDto
{
    public UserInfoDto(
        Guid id, 
        string name, 
        string phone, 
        string? address, 
        string email, 
        string username
       )
    {
        Id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.username = username;
        
    }

    public Guid Id { get; set; }
     public string name { get; set; }

     public string phone { get; set; }
    public string? address { get; set; } = "";
     public string email { get; set; }
     public string username { get; set; }
}