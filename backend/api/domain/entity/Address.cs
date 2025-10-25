using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Address
{
    [Key]
    public Guid Id { get; set; }

    public decimal? Longitude { get; set; } = null;
    public decimal? Latitude { get; set; } = null;
    
    public string Title { get; set; }

    public bool IsCurrent { get; set; } = false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;
    public Guid OwnerId { get; set; }
    
}