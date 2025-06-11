using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto.Request;

public enum enOrderItemStatusDto 
{
    Excepted,Cancelled
}

public class OrderItemUpdateDto
{
    [Required]
    public Guid id { get; set; }
    [Required]
    public enOrderItemStatusDto status { get; set; }
}