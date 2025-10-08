using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.Presentation.dto
{
    public class StoreDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string WallpaperImage  { get; set; }
        public string  SmallImage { get; set; }
        public string UserName { get; set; } = "";
        public bool  IsBlocked { get; set; }
        public decimal? Longitude { get; set; } = null;
        public decimal? Latitude { get; set; } = null;
        public Guid UserId { get; set; }
        public DateTime? UpdatedAtAt { get; set; } = null;
    }

    public class CreateStoreDto
    {
        [StringLength(maximumLength: 100, MinimumLength = 2, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; } = string.Empty;

        [Required] public  IFormFile WallpaperImage { get; set; }
        [Required] public IFormFile SmallImage { get; set; }

        [Required]
        public decimal Longitude { get; set; }
        
        [Required]
        public decimal Latitude { get; set; }
    }
    
    
    public class UpdateStoreDto
    {
        [StringLength(maximumLength:100 ,ErrorMessage= "name must not  be empty")]
        public string? Name { get; set; }
        public IFormFile? WallpaperImage { get; set; } = null;
        public IFormFile? SmallImage { get; set; } = null;
        public decimal? Longitude  { get; set; }
        public decimal? Latitude    { get; set; }
        
       
    }
    
    public class StoreStatusDto
    {
        public  Guid StoreId { get; set; }
        public   bool Status { get; set; }
    }
}