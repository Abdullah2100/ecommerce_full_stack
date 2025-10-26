using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using api.application.Interface;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;
using Microsoft.IdentityModel.Tokens;

namespace api.application
{
    public enum EnTokenMode
    {
        AccessToken,
        RefreshToken
    }

    public enum EnTokenClaimType
    {
        Email,
        Sub,
        Exp,
        Lat,
        None
    }

    public class AuthenticationServices(IConfig config) : IAuthenticationService
    {
        private static EnTokenClaimType _convertKeyToClaimType(string key)
        {
            switch (key)
            {
                case "email": return EnTokenClaimType.Email;
                case "id": return EnTokenClaimType.Sub;
                case "lat": return EnTokenClaimType.Lat;
                case "exp": return EnTokenClaimType.Exp;
                default: return EnTokenClaimType.None;
            }
        }

        public static Claim? getClaimType(IEnumerable<Claim> claim, string key)
        {
            EnTokenClaimType claimType = _convertKeyToClaimType(key);
            switch (claimType)
            {
                case EnTokenClaimType.Email:
                {
                    return claim.First(x => x.Type == "email");
                }
                case EnTokenClaimType.Sub:
                {
                    return claim.First(x => x.Type == "sub");
                }
                case EnTokenClaimType.Lat:
                {
                    return claim.First(x => x.Type == "iat");
                }
                case EnTokenClaimType.Exp:
                {
                    return claim.First(x => x.Type == "exp");
                }
                default:
                {
                    return null;
                }
            }
        }

        public string GenerateToken(Guid id, string email, EnTokenMode tokenType)
        {
            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            string key = config.getKey("credentials:key");
            string issuer = config.getKey("credentials:Issuer");
            string audience = config.getKey("credentials:Audience");

            List<Claim> claims = new List<Claim>()
            {
                new(JwtRegisteredClaimNames.Jti, ClsUtil.GenerateGuid().ToString()),
                new(JwtRegisteredClaimNames.Sub, id.ToString() ?? ""),
                new(JwtRegisteredClaimNames.Email, email)
            };

            SecurityTokenDescriptor tokenDescip = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = ClsUtil.GenerateDateTime(tokenType),
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

        public Claim? GetPayloadFromToken(string key, string token)
        {
            try
            {
                JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();

                JwtSecurityToken? jwtToken = tokenHandler.ReadJwtToken(token);
                return getClaimType(jwtToken.Claims, key);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error extracting payload: {ex.Message}");
                return null;
            }
        }
    }
}