using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class DeliveryDto
    {
        public Guid Id { get; set; }
        public Guid UserId { get; set; }
        public bool IsAvaliable { get; set; } = true;
        public DateTime CreatedAt { get; set; } = DateTime.Now;
        public string? Thumbnail { get; set; }
        public DeliveryAddressDto Address { get; set; }
        public DeliveryAnalysDto? Analys { get; set; } = null;
        public UserDeliveryInfoDto User { get; set; }
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
        [Required] public string DeviceToken { get; set; }
        [Required] public IFormFile? Thumbnail { get; set; }

        [MinLength(2, ErrorMessage = "Longitude must not be empty")]
        [Required]
        public decimal Longitude { get; set; }

        [MinLength(2, ErrorMessage = "Latitude must not be empty")]
        [Required]
        public decimal Latitude { get; set; }
    }
}