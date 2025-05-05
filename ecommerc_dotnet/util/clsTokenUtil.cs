using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace hotel_api.util;

public class clsTokenUtil
{
    public   enum enTokenClaimType
    {
       Email,Sub,Exp,Lat,None
    }
    private static enTokenClaimType _convertKeyToClaimType(string key)
    {
        switch (key)
        {
            case "email":return enTokenClaimType.Email;
            case "id": return enTokenClaimType.Sub;
            case "lat":return enTokenClaimType.Lat;
            case "exp":return enTokenClaimType.Exp;
            default:return enTokenClaimType.None;
        }
    }

    public static Claim? getClaimType(IEnumerable<Claim> claim, string key)
    {
        enTokenClaimType claimType = _convertKeyToClaimType(key);
        switch (claimType)
        {
            case enTokenClaimType.Email:
            {
                return claim.First(x => x.Type == "email");
            }
            case enTokenClaimType.Sub:
            {
                return claim.First(x => x.Type == "sub");
            }
            case enTokenClaimType.Lat:
            {
                return claim.First(x => x.Type == "iat");
            }
            case enTokenClaimType.Exp:
            {
                return claim.First(x => x.Type == "exp");
            }
            default:
            {
                return null;
            }
                
        }

    }


    public static bool isRefreshToken(string issuAt, string expireAt)
    {
        long lIssuDate = long.Parse(issuAt);
        long lExpireDate = long.Parse(expireAt);
        
        var issuDateTime = DateTimeOffset.FromUnixTimeSeconds(lIssuDate).DateTime;
        var expireTime =DateTimeOffset.FromUnixTimeSeconds(lExpireDate).DateTime;
        
        var rsult = issuDateTime-expireTime;
        return rsult.Days>=29;
    }

    // public static bool isValidToken(string token, IConfigurationServices _config)
    // {
    //     var key = _config.getKey("credentials:key");
    //     var issuer = _config.getKey("credentials:Issuer");
    //     var audience = _config.getKey("credentials:Audience");
    //     try
    //     {
    //         var tokenHandler = new JwtSecurityTokenHandler();
    //
    //
    //         var validationParameters = new TokenValidationParameters
    //         {
    //             IssuerSigningKey = new SymmetricSecurityKey( Encoding.ASCII.GetBytes(key)),
    //
    //             ValidIssuer = issuer, 
    //             ValidAudience = audience,
    //
    //             ClockSkew = TimeSpan.Zero, 
    //
    //             ValidateIssuerSigningKey = true,
    //
    //         };
    //
    //        tokenHandler.ValidateToken(token, validationParameters, out SecurityToken validatedToken);
    //
    //         Console.WriteLine("Token is valid.");
    //         return true;
    //     }
    //     catch (SecurityTokenExpiredException)
    //     {
    //         Console.WriteLine("Token is expired.");
    //         return false;
    //     }
    //     catch (SecurityTokenInvalidSignatureException)
    //     {
    //         Console.WriteLine("Invalid token signature.");
    //         return false;
    //     }
    //     catch (Exception ex)
    //     {
    //         Console.WriteLine("Token validation failed: " + ex.Message);
    //         return false;
    //     }
    // }
    //
    //
}