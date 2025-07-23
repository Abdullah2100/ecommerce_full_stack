using System.ComponentModel.DataAnnotations;
using ecommerc_dotnet.dto.Response;

namespace ecommerc_dotnet.dto
{
    public enum enOrderItemStatusDto
    {
        Excepted,
        Cancelled
    }

    public class OrderItemDto
    {
        public Guid Id { get; set; }
        public Guid OrderId { get; set; }
        public decimal Price { get; set; }
        public int Quanity { get; set; }
        public OrderProductDto? Product { get; set; }
        public List<OrderVarientDto> ProductVarient { get; set; }
        public String OrderItemStatus { get; set; } = ""; 
    }
    
    
    public class DeliveryOrderItemDto
    
    {

        public Guid Id { get; set; }
        public Guid OrderId { get; set; }
        public decimal Price { get; set; }
        public int Quanity { get; set; }
        public DeliveryAddressDto? Address { get; set; } 
        public OrderProductDto? Product { get; set; }
        public List<OrderVarientDto> productVarient { get; set; }
        public String OrderItemStatus { get; set; } = "";
    }

    
    public class CreateOrderItemDto
    {
        [Required] public Guid StoreId { get; set; }
        [Required] public decimal Price { get; set; }
        [Required] public int Quanity { get; set; } = 1;
        [Required] public Guid ProductId { get; set; }

        public List<Guid>? ProductsVarientId { get; set; } = null;
    }


    public class UpdateOrderItemDto
    {
        [Required] public Guid Id { get; set; }
        [Required] public enOrderItemStatusDto Status { get; set; }
    }

    public class OrderItemsStatusEvent
    {
        public Guid OrderId { get; set; }
        public Guid OrderItemId { get; set; }
        public string Status { get; set; }
    }
}