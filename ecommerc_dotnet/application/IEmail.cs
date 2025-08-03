namespace ecommerc_dotnet.di.email;

public interface IEmail
{
   public Task<bool>sendingEmail(string receivedEmail,string otp);
}