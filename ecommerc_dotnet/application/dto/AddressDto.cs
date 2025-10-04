using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class AddressDto
    {
        public Guid Id { get; set; }
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;
        public string Title { get; set; }
        public bool IsCurrent { get; set; } = false;
    }


    public class CreateAddressDto
    {
        [Required]
        public decimal Longitude { get; set; } = 0;

        [Required]
        public decimal Latitude { get; set; } = 0;

        [StringLength(maximumLength: 100, MinimumLength = 3, ErrorMessage = "address title must not be empty")]
        public string Title { get; set; } = string.Empty;
    }

    public class UpdateAddressDto
    {
        [Required] public Guid Id { get; set; } = Guid.Empty;
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;
        
        [StringLength(maximumLength: 100 , ErrorMessage = "address title must not be empty")]
        public string? Title { get; set; } = null;
    }
    
    
    public class DeliveryAddressDto
    {
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;
    }
    
    
}