using System.Net;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;

namespace hotel_api.Services.EmailServices;

using System.Net.Mail;

public class EmailServicesImplement : IEmailServices
{
    public EmailServicesImplement(IConfigurationServices? configurationService)
    {
        _configurationService = configurationService;
    }

    private readonly IConfigurationServices? _configurationService;


    public async Task<bool> sendingEmail(string otp, string email)
    {
        try
        {
            var server = _configurationService.getKey("server");
            var userName = _configurationService.getKey("username");
            var password = _configurationService.getKey("password");
            var port = Convert.ToInt16(_configurationService.getKey("port"));
            var client = new SmtpClient(server, port)
            {
                EnableSsl = true,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential( "ali735501225@gmail.com", password)
            };

         await client.SendMailAsync(
                new MailMessage(from: "ali735501225@gmail.com",
                    to: email,
                    "otp ",
                    $"your otp is {otp}"
                ));
         
                return true;
        }

        catch (Exception ex)
        {
            Console.WriteLine("EmailServicesImplement sending email failed");
            return false;
        }
    }
}