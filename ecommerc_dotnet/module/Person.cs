using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.module;

public class Person
{
    [Key]
    public Guid ID { get; set; }
    public string name { get; set; }
    public string phone { get; set; }
    public string?  address { get; set; }
    public string  email{ get; set; }
}