using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.Presentation.dto
{
    public class AuthDto
    {
        public string Token { get; set; }
        public string RefreshToken { get; set; }
    }

    public class SignupDto
    {
        [StringLength(maximumLength: 50, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; } = string.Empty;

        [StringLength(maximumLength: 13, MinimumLength = 9, ErrorMessage = "phone must between  9 and 13 characters")]
        [Required]
        public string Phone { get; set; } = string.Empty;

        [Required] public string Email { get; set; } = string.Empty;

        [Required] public string Password { get; set; } = string.Empty;

        public string? DeviceToken { get; set; } = null;
        public int? Role { get; set; } = 1;
    }


    public class LoginDto
    {
        [StringLength(maximumLength: 50,  ErrorMessage = "username must not be empty")]
        [Required]
        public string Username { get; set; } = string.Empty;

        [Required] public string Password { get; set; } = string.Empty;

        public string? DeviceToken { get; set; } = null;
    }

    public class ForgetPasswordDto
    {
        [Required]
        public string Email { get; set; }=string.Empty;
    }


    public class CreateVerificationDto
    {
        [Required]
        public string Email { get; set; }=String.Empty;
        [Required]
        public string Otp { get; set; }=String.Empty;
    }


    public class CreateReseatePasswordDto
    {
        [Required] public string Email { get; set; }=string.Empty;
        [Required] public string Otp { get; set; } = string.Empty;
        [Required] public string Password { get; set; }=string.Empty;
    }
}