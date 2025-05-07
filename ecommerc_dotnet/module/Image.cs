using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class Image
{
    [Key]
    public Guid? ID { get; set; }
    public string name { get; set; }
    public Guid belongTo { get; set; }
}