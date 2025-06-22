using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ecommerc_dotnet.module;

public class Order
{
    [Key]
    public Guid id { get; set; }
    public decimal longitude { get; set; }
    public decimal latitude { get; set; }
    public Guid user_id { get; set; }
    public decimal totalPrice { get; set; }
    public int status { get; set; }
    public decimal distanceToUser { get; set; } = 0;
    public Guid? delevery_id { get; set; } = null;

    [Column(TypeName = "Timestamp")]
    public DateTime created_at { get; set; } = DateTime.Now;
    
    [Column(TypeName = "Timestamp")]
    public DateTime? updated_at { get; set; } = null;
    
    public User user { get; set; }
    public Delivery? delivered_by { get; set; }
    public ICollection<OrderItem> items { get; set; }
}