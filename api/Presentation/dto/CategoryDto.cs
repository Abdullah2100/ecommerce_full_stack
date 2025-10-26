using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class CategoryDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Image { get; set; }
    }

    public class CreateCategoryDto
    {
        [StringLength(maximumLength: 50, MinimumLength = 3, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; } = string.Empty;

        [Required] public IFormFile Image { get; set; }
    }


    public class UpdateCategoryDto
    {
        [Required] public Guid Id { get; set; }
        
        [StringLength(maximumLength: 50 , ErrorMessage = "Name must not be empty")]
        public string? Name { get; set; } = null;
        public IFormFile? Image { get; set; } = null;

    }
}