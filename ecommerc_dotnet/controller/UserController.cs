using System.Security.Claims;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.dto;
using hotel_api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace ecommerc_dotnet.controller;

[Authorize]
[ApiController]
[Route("api/User")]
public class UserController(IUserServices userServices) : ControllerBase
{
    [AllowAnonymous]
    [HttpPost("signup")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> signUp([FromBody] SignupDto data)
    {
        var result = await userServices.signup(data);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> login([FromBody] LoginDto data)
    {
        var result = await userServices.login(data);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("me")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUser()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.getMe(userId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("{page:int}")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> getUsers(int page)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.getUsers(page, adminId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpDelete("{userId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> blockOrUnBlockUser(Guid userId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.blockOrUnBlockUser(adminId.Value, userId);

        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }



    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> updateUser(
        [FromForm] UpdateUserInfoDto userData
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.updateUser(userData, userId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPost("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> addNewUserAddress(
        [FromBody] CreateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.addAddressToUser(address, userId.Value);

        return result.IsSeccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpPut("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> updateUserLocation(
        [FromBody] UpdateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.updateUserAddress(address, userId.Value);


        return result.IsSeccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpDelete("address/{addressId}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> deleteUserLocation(
        Guid addressId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.deleteUserAddress(addressId, adminId.Value);


        return result.IsSeccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPatch("address/active/{addressId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> updateUserCurrentLocation(Guid addressId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = AuthinticationUtil.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.updateUserCurrentAddress(addressId, userId.Value);


        return result.IsSeccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }




    [AllowAnonymous]
    [HttpPost("generateOtp")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> generateOtp(
        [FromBody] ForgetPasswordDto otp
    )
    {
        var result = await userServices.generateOtp(otp);


        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [AllowAnonymous]
    [HttpPost("otpVerification")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> verifingOtp(
        [FromBody] CreateVerificationDto verification
    )
    {
        var result = await userServices.otpVerification(verification);


        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [AllowAnonymous]
    [HttpPost("reseatPassword")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> reseatPassword([FromBody] CreateReseatePasswordDto data)
    {
        var result = await userServices.reseatePassword(data);


        return result.IsSeccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
}