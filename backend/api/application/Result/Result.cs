namespace api.application.Result;

public class Result<T>(
    bool isSuccessful,
    string message,
    T? data,
    int statusCode)
{
    public bool IsSuccessful { get; set; } = isSuccessful;
    public string Message { get; set; } = message;
    public T? Data { get; set; } = data;
    public int StatusCode { get; set; } = statusCode;
}