using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class AddressDto
    {
        public Guid Id { get; set; }
        public decimal Longitude { get; set; }
        public decimal Latitude { get; set; }
        public string Title { get; set; }
        public bool IsCurrent { get; set; } = false;
    }


    public class CreateAddressDto
    {
        [MinLength(2,ErrorMessage= "Longitude must not be empty")]
        [Required] 
        public decimal Longitude { get; set; }
        
        [MinLength(2,ErrorMessage= "Latitude must not be empty")]
        [Required] public decimal Latitude { get; set; }

        [StringLength(maximumLength: 100, MinimumLength = 5, ErrorMessage = "address title must not be empty")]
        public string Title { get; set; }
    }

    public class UpdateAddressDto
    {
        [Required] public Guid Id { get; set; }
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;
        
        [StringLength(maximumLength: 100 , ErrorMessage = "address title must not be empty")]
        public string? Title { get; set; } = null;
    }
    
    
    public class DeliveryAddressDto
    { 
        public decimal Longitude { get; set; }
        public decimal Latitude { get; set; }
    }
    
    
}