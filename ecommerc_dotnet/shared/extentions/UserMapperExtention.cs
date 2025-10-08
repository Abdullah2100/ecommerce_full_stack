using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.dto;
using Mono.TextTemplating;

namespace ecommerc_dotnet.shared.extentions;

public static class UserMapperExtention
{
    public static UserInfoDto toUserInfoDto(this User user, string url)
    {
        return new UserInfoDto
        {
            Id = user.Id,
            Email = user.Email,
            Name = user.Name,
            Phone = user.Phone,
            Thumbnail = string.IsNullOrEmpty(user.Thumbnail) ? "" : url + user.Thumbnail,
            IsActive = user.IsBlocked == false,
            IsAdmin = user.Role == 0,
            Address = user.Addresses?.Select(ad => ad.toDto()).ToList(),
            StoreId = user?.Store?.Id
        };
    }


    public static UserDeliveryInfoDto toDeliveryInfoDto(this User user, string url)
    {
        return new UserDeliveryInfoDto
        {
            Email = user.Email,
            Name = user.Name,
            Phone = user.Phone,
            Thumbnail = string.IsNullOrEmpty(user.Thumbnail) ? "" : url + user.Thumbnail,
        };
    }

    public static bool isEmpty(this UpdateUserInfoDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name) &&
               string.IsNullOrWhiteSpace(dto.Phone) &&
               dto.Thumbnail == null &&
               string.IsNullOrWhiteSpace(dto.Password) &&
               string.IsNullOrWhiteSpace(dto.NewPassword)
            ;
    }

    public static Result<AuthDto?>? isHasStore(User user)
    {
        switch (user.Store is not null)
        {
            case true:
            {
                if (user.Store.IsBlock)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "store is Blocked",
                        isSeccessful: false,
                        statusCode: 400
                    );
                }

                return null;
            }

            default:
            {
                if (user.Store is null)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "you must has store before done this operation",
                        isSeccessful: false,
                        statusCode: 404
                    );
                }

                return null;
            }
        }
    }

    public static Result<AuthDto?>? isValidateFunc(
        this User? user,
        bool isAdmin = true,
        bool isStore = false)
    {
        if (user is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }


        //validate user if it is admin or user according to isAdmin feild 
        switch (isAdmin)
        {
            case false:
            {
                if (user.IsBlocked)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user is blocked",
                        isSeccessful: false,
                        statusCode: 404
                    );
                }

                //check if user has store
                return isStore ? isHasStore(user) : null;
            }
            default:
            {
                if (user.Role == 1 && user.IsBlocked)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user not havs the permission",
                        isSeccessful: false,
                        statusCode: 400
                    );
                }

                //check if admin has store
                return isStore ? isHasStore(user) : null;
            }
        }
    }

    public static bool isUpdateAnyFeild(this UpdateUserInfoDto dto)
    {
        return dto.Thumbnail != null ||
               !(string.IsNullOrEmpty(dto.NewPassword) && string.IsNullOrEmpty(dto.Password)) ||
               !string.IsNullOrEmpty(dto.Phone) ||
               !string.IsNullOrEmpty(dto.Name);
    }
}