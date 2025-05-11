using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class AddressResponseDto
{ 
    public Guid id { get; set; }
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    public string title { get; set; } 
    public bool isCurrent { get; set; } = false;
}