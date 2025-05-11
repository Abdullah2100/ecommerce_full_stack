using System.ComponentModel.DataAnnotations;
using NetTopologySuite.Geometries;

namespace ecommerc_dotnet.dto.Request;

public class UpdateUserInfo
{
    public Guid? userId { get; set; } = null;
    [MaxLength(50)] public string? name { get; set; } = null;
    [MaxLength(13)] public string? phone { get; set; } = null;
    // public Geometry? address { get; set; } = null;
    [MaxLength(50)] public string? username { get; set; } = null;
    public Geometry? location { get; set; } = null;
    public string? password { get; set; } = null;
    public string? newPassword { get; set; } = null;
    public IFormFile? thumbnail { get; set; } = null;
}