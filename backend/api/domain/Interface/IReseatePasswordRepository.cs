using api.domain.entity;

namespace api.domain.Interface;

public interface IReseatePasswordRepository:IRepository<ReseatePasswordOtp>
{
    Task<bool> IsExist(string otp,string email);
    Task<ReseatePasswordOtp?> GetOtp(string otp,string email,bool state=false);
    Task<ReseatePasswordOtp?> GetOtp(string otp);
    void Delete(Guid id);
}