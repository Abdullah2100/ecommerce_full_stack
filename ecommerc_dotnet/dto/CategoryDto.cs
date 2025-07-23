using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class CategoryDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Image { get; set; }
    }

    public class CreateCategoryto
    {
        [StringLength(maximumLength: 50, MinimumLength = 10, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; }

        [Required] public IFormFile Image { get; set; }
    }


    public class UpdateCategoryDto
    {
        [Required] public Guid Id { get; set; }
        
        [StringLength(maximumLength: 50 , ErrorMessage = "Name must not be empty")]
        public string? Name { get; set; } = null;
        public IFormFile? Image { get; set; } = null;

        public bool IsEmpty() => string.IsNullOrWhiteSpace(Name) && Image == null;
    }
}