using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.Presentation.dto.Request;
using ecommerc_dotnet.Presentation.dto.Response;

namespace ecommerc_dotnet.Presentation.dto
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
        public int Status { get; set; }
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

    public class OrderTakedByEvent
    {
        public Guid Id { get; set; }
        public Guid DeliveryId { get; set; }
    }

    public class UpdateOrderStatusDto
    {
        public Guid Id { get; set; }
        public int Status { get; set; } 
    }
}