namespace ecommerc_dotnet.di.email;

public interface IMessageSerivice
{
    Task<bool> sendingMessage(string message, string from);
}

public interface IEmail : IMessageSerivice
{
}