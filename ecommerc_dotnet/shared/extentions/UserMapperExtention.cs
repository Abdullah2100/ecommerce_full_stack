using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.shared.extentions;

public static class UserMapperExtention 
{
    public static UserInfoDto toUserInfoDto(this User user,string url)
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
    
    

    public static UserDeliveryInfoDto toDeliveryInfoDto(this User user,string url)
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
               dto.Thumbnail == null&&
               string.IsNullOrWhiteSpace(dto.Password) && 
               string.IsNullOrWhiteSpace(dto.NewPassword)
            ;
    }
    
    public static Result<AuthDto?>? isValideFunc(this User? user, bool isAdmin = true)
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

        switch (!isAdmin)
        {
            case true:
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

                return null;
            }
            default:
            {
                if (user.Role == 1)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user not havs the permission",
                        isSeccessful: false,
                        statusCode: 400
                    );
                }

                return null;
            }
        }
    }

}