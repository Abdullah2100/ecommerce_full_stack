using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class OrderDto
    {
        public Guid Id { get; set; }
        public decimal Longitude { get; set; }
        public decimal Latitude { get; set; }
        public decimal TotalPrice { get; set; }
        public decimal DeliveryFee { get; set; }
        public String Name { get; set; }
        public String UserPhone { get; set; }
        public string Status { get; set; }
        public List<OrderItemDto>? OrderItems { get; set; } = null;
    }

    public class AdminOrderDto
    {
        public List<OrderDto>? Orders { get; set; }
        public int pageNum { get; set; } = 1;
    }
    public class DeliveryOrderDto
    {
        public Guid Id { get; set; }
        public decimal Longitude { get; set; }
        public decimal Latitude { get; set; }
        public decimal TotalPrice { get; set; }
        public decimal RealPrice { get; set; }
        public decimal DeliveryFee { get; set; }
        public String Name { get; set; }
        public String UserPhone { get; set; }
        public int Status { get; set; }
        public List<DeliveryOrderItemDto> OrderItems { get; set; }
    }

    public class CreateOrderDto
    {
        [Required] public decimal Longitude { get; set; }
        [Required] public decimal Latitude { get; set; }
        [Required] public decimal TotalPrice { get; set; }
        [Required] public List<CreateOrderItemDto> Items { get; set; }
    }

    public class OrderTookByEvent
    {
        public Guid Id { get; set; }
        public Guid DeliveryId { get; set; }
    }

    public class UpdateOrderStatusDto
    {
        public Guid Id { get; set; }
        public int Status { get; } 
    }
    
    public class UpdateOrderStatusEventDto
    {
        public Guid Id { get; set; }
        public string Status { get; set; } 
    }
}