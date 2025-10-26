using System.Security.Claims;
using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IRefreshTokenServices
{
    Task<Result<AuthDto?>> GenerateRefreshToken(string token,Claim? id,Claim? issuAt,Claim? expireAt);
}