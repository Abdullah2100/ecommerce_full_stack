using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class CreateBannerDto
    {
        [Required] public IFormFile Image { get; set; }
        [Required] public DateTime EndAt { get; set; }
    }

    public class BannerDto
    {
        public Guid Id { get; set; }
        public String Image { get; set; }
        public DateTime EndAt { get; set; }
        public DateTime CreatedAt { get; set; }
        public Guid StoreId { get; set; }
    }
}