using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.dto
{
    public class AuthDto
    {
        public string Token { get; set; }
        public string RefreshToken { get; set; }
    }

    public class SignupDto
    {
        [StringLength(maximumLength: 50, MinimumLength = 10, ErrorMessage = "Name must not be empty")]
        [Required]
        public string Name { get; set; }

        [StringLength(maximumLength: 13, MinimumLength = 9, ErrorMessage = "phone must between  9 and 13 characters")]
        [Required]
        public string Phone { get; set; }

        [Required]
        public string Email { get; set; }

        [Required]
        public string Password { get; set; }

        public string? DeviceToken { get; set; } = null;
        public int? Role { get; set; } = 1;
    }


    public class LoginDto
    {
        [StringLength(maximumLength:50, MinimumLength = 10, ErrorMessage = "username must not be empty")]
        [Required]
        public string Username { get; set; }

        [Required]
        public string Password { get; set; }

        public string DeviceToken { get; set; }
    }

    public class ForgetPasswordDto
    {
        public string Email { get; set; }
    }


    public class VerificationDto
    {
        public string Email { get; set; }
        public string Otp { get; set; }
    }
    
    
    public class CreateReseatePasswordDto 
    {
        [Required]
        public string Email { get; set; }
        [Required]
        public string Otp { get; set; }
        [Required]
        public string Password { get; set; }
    }
   
    
}