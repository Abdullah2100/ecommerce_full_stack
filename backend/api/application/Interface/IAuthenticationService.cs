using System.Security.Claims;
using ecommerc_dotnet.application;

namespace api.application.Interface;

public interface IAuthenticationService
{
   string GenerateToken(Guid id, string email, EnTokenMode tokenType=EnTokenMode.AccessToken);
   Claim? GetPayloadFromToken(string key, string token);
}