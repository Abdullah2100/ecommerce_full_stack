namespace ecommerc_dotnet.application.Result;

public class Result<T>
{
    public Result(
        bool isSuccessful,
        string message,
        T? data,
        int statusCode
    )
    {
        IsSuccessful = isSuccessful;
        Message = message;
        Data = data;
        StatusCode = statusCode;
    }

    public bool IsSuccessful { get; set; }
    public string Message { get; set; }
    public T? Data { get; set; }
    public int StatusCode { get; set; }
}