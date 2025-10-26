using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Delivery
{
    [Key] public Guid Id { get; set; }
    public Guid UserId { get; set; }
    public string? DeviceToken { get; set; } = null;

    public bool IsAvailable { get; set; } = true;

    public bool IsBlocked { get; set; } = false;


    [Column(TypeName = "Timestamp")] public DateTime CreatedAt { get; set; } = DateTime.Now;


    [Column(TypeName = "Timestamp")] public DateTime? UpdatedAt { get; set; } = null;

    public string? Thumbnail { get; set; }
    public Guid? BelongTo { get; set; } = null;
    public Address? Address { get; set; }
    public User User { get; set; }
    public ICollection<Order>? Orders { get; set; }
}