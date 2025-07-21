using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class AddressResponseDeliveryDto
{ 
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
}