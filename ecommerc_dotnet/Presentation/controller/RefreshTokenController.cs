using System.Security.Claims;
using ecommerc_dotnet.application.Interface;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.Presentation.controller;

[ApiController]
[Route("api/RefreshToken")]
public class RefreshTokenController(
    IAuthenticationService authenticationService,
    IRefreshTokenServices refreshTokenServices): ControllerBase
{
    [HttpPost("{token}")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> createBanner(string token)
    {
        Claim? id = authenticationService.getPayloadFromToken("id",
            token.Replace("Bearer ", ""));
        Claim? issuAt = authenticationService.getPayloadFromToken("exp",
            token.Replace("Bearer ", ""));
        Claim? expire = authenticationService.getPayloadFromToken("lat",
            token.Replace("Bearer ", ""));
       
        var result = await refreshTokenServices.generateRefreshToken(token, id, issuAt, expire);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


}