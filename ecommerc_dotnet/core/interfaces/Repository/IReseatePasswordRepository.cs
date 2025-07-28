using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.core.interfaces.Repository;

public interface IReseatePasswordRepository:IRepository<ReseatePasswordOtp>
{
    Task<bool> isExist(string otp);
    Task<ReseatePasswordOtp?> getOtp(string otp,string email);
    Task<ReseatePasswordOtp?> getOtp(string otp);
}