using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class VariantDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
    }


    public class CreateVariantDto
    {
        [StringLength(maximumLength: 40, MinimumLength = 3, ErrorMessage = "Enter Valide Name")]
        [Required]
        public string Name { get; set; } = string.Empty;
    }
    
    public class UpdateVariantDto
    {
        [Required]
        public Guid Id { get; set; }
        
        [StringLength(maximumLength: 40 , ErrorMessage = "Enter Valide Name")]
        public string? Name { get; set; } = null;
    }
    
}