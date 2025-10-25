using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    
    
    public class ProductDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string  Thmbnail { get; set; }
        public Guid SubcategoryId { get; set; }
        public Guid CategoryId { get; set; }
        public Guid StoreId { get; set; }
        public decimal Price { get; set; }
        public List<List<ProductVariantDto>>? ProductVarients { get; set; }
        public List<string> ProductImages { get; set; }
    }
    
    public class CreateProductDto
    {
        [StringLength(maximumLength: 100, MinimumLength = 5, ErrorMessage = "name must not be empty")]
        public string Name { get; set; }
        public string Description { get; set; }
        public IFormFile Thmbnail { get; set; }
        public Guid SubcategoryId { get; set; }
        public Guid StoreId { get; set; }
        public decimal Price { get; set; }
        public List<CreateProductVariantDto>? ProductVarients { get; set; } = null;
        public List<IFormFile> Images { get; set; }
    }
    
    
    public class UpdateProductDto 
    {
        [Required]public Guid Id { get; set; }
        
        [StringLength(maximumLength: 100 , ErrorMessage = "name must not be empty")]
        public string? Name { get; set; }= null;
        
        public string? Description { get; set; }= null;
        public IFormFile?  Thmbnail { get; set; }= null;
        public Guid? SubcategoryId { get; set; }= null;
        [Required] public Guid StoreId { get; set; }
        public decimal? Price { get; set; }= null;
        public List<CreateProductVariantDto>? ProductVarients { get; set; } = null;
        public List<CreateProductVariantDto>? DeletedProductVarients { get; set; } = null;
        public List<IFormFile>? Images { get; set; }= null;
        public List<string>? Deletedimages { get; set; }= null;

     
    }
    
    
    public class AdminProductsDto
    {
        public Guid Id { get; set; }
        
        [StringLength(maximumLength: 100 , ErrorMessage = "name must not be empty")]
        public string Name { get; set; }
        
        public string Description { get; set; }
        public string  Thmbnail { get; set; }
        public string Subcategory { get; set; }
        public string StoreName { get; set; }
        public decimal Price { get; set; }
        public List<List<AdminProductVarientDto>>? ProductVarients { get; set; }
        public List<string> ProductImages { get; set; } 
    }
}