using System.Text.Json;

namespace ecommerc_dotnet.dto;

public class ErrorDetailsDto
{
    public string Message { get; set; }
    public int StatusCode { get; set; }
    public override string ToString()
    {
            return JsonSerializer.Serialize(this); 
    }

}