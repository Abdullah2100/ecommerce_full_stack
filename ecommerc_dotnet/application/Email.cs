using System.Net;
using System.Net.Mail;
using System.Text;
using System.Text.Json;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.midleware.ConfigImplment;
using Exception = System.Exception;

namespace hotel_api.Services.EmailService;

public class Email(IConfig config) : IEmail
{
    public async Task<bool> sendingMessage(string message, string to)
    {
        try
        {
            var serverUrl = config.getKey("smtp_data:url");
            var userName = config.getKey("smtp_data:username");
            var password = config.getKey("smtp_data:password");
            var port = config.getKey("smtp_data:port");

            var client = new SmtpClient(serverUrl, Convert.ToInt32((port)))
            {
                EnableSsl = true,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(userName, password)
            };
            await client.SendMailAsync(
                new MailMessage(
                    userName
                    , to,
                    "Otp Valdiation",
                    message)
            );
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from sending otp to user " + ex.Message);
            return false;
        }
    }
}