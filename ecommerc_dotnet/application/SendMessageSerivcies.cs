using ecommerc_dotnet.di.email;
using Microsoft.DotNet.Scaffolding.Shared.Messaging;

namespace ecommerc_dotnet.application;

public class SendMessageSerivcies(IMessageSerivice messageSerivice)
{
    public async Task<bool> sendMessage(string message, string to)
    {
        return await messageSerivice.sendingMessage(message, to);
    }
}