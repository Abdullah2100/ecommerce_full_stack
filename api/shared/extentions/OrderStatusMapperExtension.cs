namespace api.shared.extentions;

public static class  OrderStatusMapperExtension
{
    public static string ToOrderStatusName(this int orderStatus)
    {
        return orderStatus switch
        {
            0 => "Regected",
            1 => "Inprogress",
            2 => "Excpected",
            3 => "Inway",
            4 => "Received",
            _ => "Completed",
        };
    }
}