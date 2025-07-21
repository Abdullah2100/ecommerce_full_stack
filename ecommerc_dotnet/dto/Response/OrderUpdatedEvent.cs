using ecommerc_dotnet.module;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Response;

public class OrderUpdatedEvent 
{ 
    public Guid id { get; set; }
    public Guid deliveryId { get; set; } 
}