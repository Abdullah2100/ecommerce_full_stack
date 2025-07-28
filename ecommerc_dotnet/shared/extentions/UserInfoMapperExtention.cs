using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.shared.extentions;

public static class UserInfoMapperExtention 
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
}