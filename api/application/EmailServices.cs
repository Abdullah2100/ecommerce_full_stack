using System.Net;
using System.Net.Mail;
using api.Infrastructure;
using ecommerc_dotnet.midleware.ConfigImplment;
using Exception = System.Exception;

namespace api.application;

public class EmailServices(IConfig config) : IMessageService 
{
    public async Task<bool> SendingMessage(string message, string to)
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