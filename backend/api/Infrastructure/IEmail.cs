namespace api.Infrastructure;

public interface IMessageService
{
    Task<bool> SendingMessage(string message, string to);
}
