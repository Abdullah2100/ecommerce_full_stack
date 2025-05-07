using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Drawing;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.module;

public class Address
{
    [Key]
    public Guid id { get; set; }
    
    [Column(TypeName = "GEOMETRY(Point, 4326)")]
    public Geometry location { get; set; }
    public Guid owner_id { get; set; }
    public User? owner { get; set; }
}