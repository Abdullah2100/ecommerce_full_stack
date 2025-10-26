using api.Infrastructure;

namespace api.application;

public class SendMessageServices(IMessageService messageService)
{
    public async Task<bool> SendMessage(string message, string to)
    {
        return await messageService.SendingMessage(message, to);
    }
}