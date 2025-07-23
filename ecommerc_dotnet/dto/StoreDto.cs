using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
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
        public DateTime UpdatedAtAt { get; set; } = DateTime.Now;
    }

    public class CreateStoreDto
    {
        [StringLength(maximumLength: 100, MinimumLength = 5, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; }

        [Required] public IFormFile WallpaperImage { get; set; }
        [Required] public IFormFile SmallImage { get; set; }
        public Guid? UserId { get; set; } = null;

        [MinLength(2, ErrorMessage = "Longitude must not be empty")]
        [Required]
        public decimal Longitude { get; set; }

        [MinLength(2, ErrorMessage = "Latitude must not be empty")]
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
        
        public bool IsEmpty()=>string.IsNullOrWhiteSpace(Name)&&
        WallpaperImage==null&&
        SmallImage==null &&
        Longitude==null &&
        Latitude==null
        ;
    }
    
    public class StoreStatusDto
    {
        public  Guid StoreId { get; set; }
        public   bool Status { get; set; }
    }
}