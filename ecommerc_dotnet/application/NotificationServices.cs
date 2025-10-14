using ecommerc_dotnet.di.email;
using FirebaseAdmin.Messaging ;
namespace ecommerc_dotnet.application;

public class NotificationServices:IMessageSerivice
{
    public async Task<bool> sendingMessage(string message, string to)
    {
        var messageObj = new Message
        {
            Notification = new Notification()
            {
                Title =message,
            },
            Token = to 
        };

        var response = await FirebaseMessaging.DefaultInstance.SendAsync(messageObj);

      return  (response is null) switch
        {
            true => true,
            _ => false,
        };

    }
}