using ecommerc_dotnet.midleware.ConfigImplment;

namespace hotel_api.Services.EmailService;

public interface iEmailService
{
   public Task<bool>sendingEmail(string receivedEmail,string otp);
}