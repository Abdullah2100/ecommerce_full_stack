namespace ecommerc_dotnet.application.Result;

public class Result<T>
{
    public Result(
        bool isSeccessful,
        string message,
        T? data,
        int statusCode
    )
    {
        IsSeccessful = isSeccessful;
        Message = message;
        Data = data;
        StatusCode = statusCode;
    }

    public bool IsSeccessful { get; set; }
    public string Message { get; set; }
    public T? Data { get; set; }
    public int StatusCode { get; set; }
}