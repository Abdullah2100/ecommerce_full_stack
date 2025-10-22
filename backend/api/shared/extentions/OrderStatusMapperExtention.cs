using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.Presentation.dto;

namespace ecommerc_dotnet.shared.extentions;

public static class  OrderStatusMapperExtention
{
    public static string toOrderStatusName(this int orderStatus)
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