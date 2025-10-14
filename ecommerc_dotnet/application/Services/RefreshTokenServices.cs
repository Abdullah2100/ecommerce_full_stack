using System.Security.Claims;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.shared.extentions;

namespace ecommerc_dotnet.application.Services;

public class RefreshTokenServices(
    IUnitOfWork unitOfWork,
    IAuthenticationService authenticationService) : IRefreshTokenServices
{
    public bool isRefreshToken(string issueAt, string expireAt)
    {
        long lIssueDate = long.Parse(issueAt);
        long lExpireDate = long.Parse(expireAt);

        var issueDateTime = DateTimeOffset.FromUnixTimeSeconds(lIssueDate).DateTime;
        var expireTime = DateTimeOffset.FromUnixTimeSeconds(lExpireDate).DateTime;

        var result = issueDateTime - expireTime;
        return result.Days >= 29;
    }

    public async Task<Result<AuthDto?>> generateRefreshToken(
        string token,
        Claim? id,
        Claim? issuAt,
        Claim? expireAt)
    {
        if (id is null || issuAt is null || expireAt is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSeccessful: false,
                statusCode: 400
            );
        }

        var idHolder = Guid.Parse(id.Value);
        User? user = await unitOfWork.UserRepository
            .getUser(idHolder);
        
        Delivery? delivery = await unitOfWork.DeliveryRepository.getDelivery(idHolder);

        var validation = user.isValidateFunc(false);
        if (validation is not null && delivery is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: validation.Message,
                isSeccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (!isRefreshToken(issuAt.Value, expireAt.Value))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "send valid token ",
                isSeccessful: false,
                statusCode: 400
            );
        }

        var tokenHolder = authenticationService.generateToken(
            id: idHolder,
            email: (user?.Email??delivery?.User?.Email) ?? string.Empty);

        var refreshTokenHolder = authenticationService.generateToken(
            id: idHolder,
            email: user?.Email?? (delivery?.User?.Email) ?? string.Empty,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSeccessful: true,
            data: new AuthDto { RefreshToken = refreshTokenHolder, Token = tokenHolder },
            message: "",
            statusCode: 200
        );
    }
}