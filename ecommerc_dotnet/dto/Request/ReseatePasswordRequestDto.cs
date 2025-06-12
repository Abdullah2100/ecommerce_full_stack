namespace ecommerc_dotnet.dto.Request;

public class ReseatePasswordRequestDto 
{
    public string email { get; set; }
    public string otp { get; set; }
    public string password { get; set; }
}