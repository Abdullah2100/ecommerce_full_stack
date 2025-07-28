using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using ecommerc_dotnet.midleware.ConfigImplment;
using hotel_api.util;
using Microsoft.IdentityModel.Tokens;

namespace hotel_api.Services
{

    public enum EnTokenMode
    {
        AccessToken,
        RefreshToken
    }
    
    public static class AuthinticationServices
    {
        public static string generateToken(
            Guid? userId, string email,
            IConfig config,
            EnTokenMode enMode = EnTokenMode.AccessToken
        )
        {
            if (userId is null) return "";
            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            string key = config.getKey("credentials:key");
            string issuer = config.getKey("credentials:Issuer");
            string audience = config.getKey("credentials:Audience");

            List<Claim> claims = new List<Claim>()
            {
                new(JwtRegisteredClaimNames.Jti, clsUtil.generateGuid().ToString()),
                new(JwtRegisteredClaimNames.Sub, userId.ToString() ?? ""),
                new(JwtRegisteredClaimNames.Email, email)
            };

            SecurityTokenDescriptor tokenDescip = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = clsUtil.generateDateTime(enMode),
                Issuer = issuer,
                Audience = audience,
                SigningCredentials = new SigningCredentials(
                    new SymmetricSecurityKey(
                        System.Text.Encoding.UTF8.GetBytes(key))
                    , SecurityAlgorithms.HmacSha256Signature)
            };


            SecurityToken? token = tokenHandler.CreateToken(tokenDescip);
            return tokenHandler.WriteToken(token);
        }


        public static Claim? GetPayloadFromToken(string key, string token)
        {
            try
            {
                JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();

                JwtSecurityToken? jwtToken = tokenHandler.ReadJwtToken(token);
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