using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class DeliveryDto
    {
        public Guid Id { get; set; }
        public Guid UserId { get; set; }
        public bool IsAvailable { get; set; } = true;
        public DateTime? UpdatedAt { get; set; } = null;
        public string? Thumbnail { get; set; }
        public DeliveryAddressDto? Address { get; set; } = null;
        public DeliveryAnalysDto? Analys { get; set; } = null;
        public UserDeliveryInfoDto? User { get; set; } = null;
    }


    public class DeliveryAnalysDto
    {
        public decimal? DayFee { get; set; } = null;
        public decimal? WeekFee { get; set; } = null;
        public decimal? MonthFee { get; set; } = null;
        public int? DayOrder { get; set; } = null;
        public int? WeekOrder { get; set; } = null;
    }

    public class CreateDeliveryDto
    {
        [Required] public Guid UserId { get; set; }
        public IFormFile? Thumbnail { get; set; } = null;
    }
    
    public class UpdateDeliveryDto
    {
        public IFormFile? Thumbnail { get; set; } = null;
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;

        [StringLength(maximumLength: 50 , ErrorMessage = "Enter Valide Name")]
        public string? Name { get; set; } = null;
        
        [StringLength(maximumLength: 13, ErrorMessage = "Enter Valide Name")]
        public string? Phone { get; set; } = null;
        public IFormFile? UserThumbnail { get; set; } = null;
        public string? Password { get; set; } = null;
        public string? NewPassword { get; set; } = null;
    }
}