using System.Security.Claims;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.application.Interface;

public interface IRefreshTokenServices
{
    Task<Result<AuthDto?>> generateRefreshToken(string token,Claim? id,Claim? issuAt,Claim? expireAt);
}