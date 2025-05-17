namespace hotel_api.Services.EmailServices;

public interface IEmailServices
{
    public  Task<bool> sendingEmail(string otp,string email);
}