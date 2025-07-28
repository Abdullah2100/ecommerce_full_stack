using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Order
{
    [Key]
    public Guid Id { get; set; }
    public decimal Longitude { get; set; }
    public decimal Latitude { get; set; }
    public Guid UserId { get; set; }
    public decimal TotalPrice { get; set; }
    public int Status { get; set; }
    
    public decimal DistanceToUser { get; set; } = 0;
  
    public decimal DistanceFee { get; set; } = 0;
    
    public Guid? DeleveryId { get; set; } = null;

    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;
    
    [Column(TypeName = "Timestamp")]
    public DateTime? UpdatedAt { get; set; } = null;
    
    public User User { get; set; }
    public Delivery? DeliveredBy { get; set; }
    public ICollection<OrderItem> Items { get; set; }
}