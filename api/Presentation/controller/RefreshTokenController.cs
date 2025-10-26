using System.Security.Claims;
using api.application.Interface;
using Microsoft.AspNetCore.Mvc;

namespace api.Presentation.controller;

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
    public async Task<IActionResult> CreateBanner(string token)
    {
        Claim? id = authenticationService.GetPayloadFromToken("id",
            token.Replace("Bearer ", ""));
        Claim? issuAt = authenticationService.GetPayloadFromToken("exp",
            token.Replace("Bearer ", ""));
        Claim? expire = authenticationService.GetPayloadFromToken("lat",
            token.Replace("Bearer ", ""));
       
        var result = await refreshTokenServices.GenerateRefreshToken(token, id, issuAt, expire);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


}