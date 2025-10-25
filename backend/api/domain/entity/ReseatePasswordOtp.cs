using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class ReseatePasswordOtp
{
    public Guid Id { get; set; }
    public string Email { get; set; }
    public string Otp { get; set; }
    public bool IsValidated { get; set; } = false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; }
}