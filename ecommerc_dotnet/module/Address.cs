using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Drawing;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.module;

public class Address
{
    [Key]
    public Guid id { get; set; }
    
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    
    public string title { get; set; }

    public bool isCurrent { get; set; } = false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime createdAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? updatedAt { get; set; } = null;
    public Guid ownerId { get; set; }
    
}