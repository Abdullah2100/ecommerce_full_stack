using Microsoft.Build.Framework;

namespace ecommerc_dotnet.dto.Request
{
    public class GeneralSettingDto
    {
        public string Name { get; set; }
        public decimal Value { get; set; }
    }
    public class UpdateGeneralSettingDto
    {
        public string? Name { get; set; } = null;
        public decimal? Value { get; set; } = null;
    }


    public class CreateGeneralSettingDto
    {
        [Required] public Guid Id { get; set; }
        [Required] public string Name { get; set; }
        [Required] public decimal Value { get; set; }
    }
}