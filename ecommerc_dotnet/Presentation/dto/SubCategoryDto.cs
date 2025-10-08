using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.Presentation.dto
{

public class SubCategoryDto
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public Guid CategoryId { get; set; } 
    public Guid StoreId { get; set; } 

}

public class CreateSubCategoryDto
{
    [Required]
    public string Name { get; set; } = String.Empty;
    [Required]
    public Guid CategoryId { get; set; } 
}

public class UpdateSubCategoryDto
{
    [Required]   public Guid Id { get; set; }
    
    [StringLength(maximumLength:50,ErrorMessage= "name must not  be empty")]
    public string? Name { get; set; } = null;
    public Guid? CategoryId { get; set; } = null;
   
}


    
}
