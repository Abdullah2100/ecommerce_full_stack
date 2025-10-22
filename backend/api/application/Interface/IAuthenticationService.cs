using System.Security.Claims;

namespace ecommerc_dotnet.application.Interface;

public interface IAuthenticationService
{
   string generateToken(Guid id, string email, EnTokenMode tokenType=EnTokenMode.AccessToken);
   Claim? getPayloadFromToken(string key, string token);
}