using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Drawing;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.module;

public class Address
{
    [Key]
    public Guid Id { get; set; }
    
    public decimal Longitude { get; set; }
    public decimal Latitude { get; set; }
    
    public string Title { get; set; }

    public bool IsCurrent { get; set; } = false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;
    public Guid OwnerId { get; set; }
    
}