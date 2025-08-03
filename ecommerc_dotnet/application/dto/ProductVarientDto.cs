using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class ProductVarientDto
    {
        public Guid Id { get; set; }
        public Guid ProductId { get; set; }
        public string? Name { get; set; }
        public decimal Precentage { get; set; }
        public Guid VarientId { get; set; }
    }
    
    public class CreateProductVarientDto
    {
        [StringLength(maximumLength:50,MinimumLength =3 ,ErrorMessage= "name must not be empty")]
        public string Name { get; set; }
        public decimal Precentage { get; set; } = 1;
        public Guid VarientId { get; set; }
    }
    
    public class AdminProductVarientDto
    {
        [StringLength(maximumLength:50 ,ErrorMessage= "name must not  be empty")]
        public string? Name { get; set; }
        public decimal Precentage { get; set; }
        public string?  VarientName { get; set; } 
    }
}