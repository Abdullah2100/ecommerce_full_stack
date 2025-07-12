using System.ComponentModel.DataAnnotations;

namespace ecommerc_dotnet.Validation;
[AttributeUsage( AttributeTargets.Property | 
                 AttributeTargets.Field,AllowMultiple = false)]
public sealed class StringNotNullOrEmptyValidation:ValidationAttribute
{

    public override bool IsValid(object? value)
    {
        string? result = value as string;
        if (result is null || result.Trim().Length == 0)return false;
        return  true;
    }
}