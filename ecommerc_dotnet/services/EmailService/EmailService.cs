using System.Net;
using System.Net.Mail;
using System.Text;
using System.Text.Json;
using ecommerc_dotnet.midleware.ConfigImplment;
using Exception = System.Exception;

namespace hotel_api.Services.EmailService;

public class EmailService :iEmailService
{
    private readonly IConfig _config;

  public  EmailService(IConfig config)
    {
        _config=config;
    }
    public async Task<bool>sendingEmail( string receivedEmail, string otp)
    {
        try
        {
            var serverUrl = _config.getKey("smtp_data:url");
            var userName = _config.getKey("smtp_data:username");
            var password = _config.getKey("smtp_data:password");
            var port =_config.getKey("smtp_data:port");
            
            var client = new SmtpClient(serverUrl, Convert.ToInt32((port)))
            {
                EnableSsl = true,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(userName, password)

            };
            await client.SendMailAsync(
                new MailMessage(
                    userName
                    ,receivedEmail,
                    "Otp Valdiation",
                    otp)
                );
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from sending otp to user "+ex.Message);
            return false;
        }
    }
}