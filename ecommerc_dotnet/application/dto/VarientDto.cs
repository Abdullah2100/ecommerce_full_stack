using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class VarientDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
    }


    public class CreateVarientDto
    {
        [StringLength(maximumLength: 40, MinimumLength = 3, ErrorMessage = "Enter Valide Name")]
        [Required]
        public string Name { get; set; } = string.Empty;
    }
    
    public class UpdateVarientDto
    {
        [Required]
        public Guid Id { get; set; }
        
        [StringLength(maximumLength: 40 , ErrorMessage = "Enter Valide Name")]
        public string? Name { get; set; } = null;
    }
    
}