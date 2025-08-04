using ecommerc_dotnet.module;

namespace ecommerc_dotnet.application.Repository;

public interface IReseatePasswordRepository:IRepository<ReseatePasswordOtp>
{
    Task<bool> isExist(string otp,string email);
    Task<ReseatePasswordOtp?> getOtp(string otp,string email,bool state=false);
    Task<ReseatePasswordOtp?> getOtp(string otp);
}