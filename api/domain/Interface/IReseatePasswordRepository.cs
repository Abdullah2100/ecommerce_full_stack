using api.domain.entity;

namespace api.domain.Interface;

public interface IReseatePasswordRepository:IRepository<ReseatPasswordOtp>
{
    Task<bool> IsExist(string otp,string email);
    Task<ReseatPasswordOtp?> GetOtp(string otp,string email,bool state=false);
    Task<ReseatPasswordOtp?> GetOtp(string otp);
    void Delete(Guid id);
}