using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using ecommerc_dotnet.midleware.ConfigImplment;
using hotel_api.util;

using Microsoft.IdentityModel.Tokens;

namespace hotel_api.Services
{
    public class AuthinticationServices
    {
      
        public enum enTokenMode{AccessToken,RefreshToken}
       
        public static string generateToken(
                Guid? userID,string email,
                IConfig config,
                enTokenMode enTokenMode=enTokenMode.AccessToken
        )
        {
            if (userID == null) return "";
            var tokenHandler = new JwtSecurityTokenHandler();
            var key = config.getKey("credentials:key");
            var issuer = config.getKey("credentials:Issuer");
            var audience = config.getKey("credentials:Audience");

            var claims = new List<Claim>(){
                new (JwtRegisteredClaimNames.Jti,clsUtil.generateGuid().ToString()),
                new (JwtRegisteredClaimNames.Sub,userID.ToString()??""),
                new (JwtRegisteredClaimNames.Email,email)
            };

            var tokenDescip = new SecurityTokenDescriptor{
                Subject = new ClaimsIdentity(claims),
                Expires = clsUtil.generateDateTime(enTokenMode),
                Issuer = issuer,
                Audience = audience,
                SigningCredentials = new SigningCredentials(
                    new SymmetricSecurityKey(
                        System.Text.Encoding.UTF8.GetBytes(key))
                        ,SecurityAlgorithms.HmacSha256Signature) 
            };
            
          
            var token = tokenHandler.CreateToken(tokenDescip);
            return tokenHandler.WriteToken(token);

        }
     
      
        public static Claim?  GetPayloadFromToken(string key,string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();
        
                var jwtToken = tokenHandler.ReadJwtToken(token);
                return clsTokenUtil.getClaimType(jwtToken.Claims, key);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error extracting payload: {ex.Message}");
                return null;
            }
        }

     
    }
}