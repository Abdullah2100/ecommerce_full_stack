using System.Security.Claims;

namespace api.application.Interface;

public interface IAuthenticationService
{
   string GenerateToken(Guid id, string email, EnTokenMode tokenType=EnTokenMode.AccessToken);
   Claim? GetPayloadFromToken(string key, string token);
}