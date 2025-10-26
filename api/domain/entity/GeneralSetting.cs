using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class GeneralSetting
{
    [Key]
    public Guid Id { get; set; }
    
    public string Name { get; set; }
    public decimal Value { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;
    
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;


}