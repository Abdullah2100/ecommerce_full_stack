using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
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
    [StringLength(maximumLength:50,MinimumLength = 4,ErrorMessage= "name must not  be empty")]
    [Required]
    public string Name { get; set; }
    [Required]
    public Guid CategoryId { get; set; } 
}

public class UpdateSubCategoryDto
{
    [Required]   public Guid id { get; set; }
    
    [StringLength(maximumLength:50,ErrorMessage= "name must not  be empty")]
    public string? Name { get; set; } = null;
    public Guid? CateogyId { get; set; } = null;
   
}


    
}
