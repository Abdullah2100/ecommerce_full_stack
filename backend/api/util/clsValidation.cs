using System.Text.RegularExpressions;

namespace api.util;

sealed class ClsValidation
{
    public static string? ValidateInput(string? phone,
        string? email,
        string? password,
        bool? isNeedValidateOther=true,
        string username = "",
        string name = "")
    {
        if (email !=  null && !IsValidEmail(email))
            return "ادخل ايميل صحيح";
        if (password !=  null && !IsValidPassword(password))
            return "ادخل كلمة مرور مناسبة";
        if (phone !=  null && !IsValidPhone(phone))
            return "ادخل رقم هاتف صحيح";
        if (isNeedValidateOther==true&&username.Length <= 0)
            return "اسم المستخدم لا يمكن ان يكون فارغا";
        if (isNeedValidateOther==true&&name.Length <= 0)
            return "الاسم لا يمكو ان يكون فارغا";
        return null;
    }
    
    public static string? ValidateInput(
        string? email=null,
        String? password=null,
        string? phone=null)
    {
        if (email !=  null && !IsValidEmail(email))
            return "ادخل ايميل صحيح";
        if (password !=  null && !IsValidPassword(password))
            return "ادخل كلمة مرور مناسبة";
        if (phone !=  null && !IsValidPhone(phone))
            return "ادخل رقم هاتف صحيح";
       
        return null;
    }


    private static bool IsValidPhone(string? phone)
    {
        if (phone is null) return false;
        return Regex.Match(phone, @"^\+?\d{9,15}$").Success;
    }

    private static bool IsValidEmail(string? email)
    {
        if (email is null) return false;
        return Regex.Match(email, @"^[a-zA-Z0-9._%±]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$").Success;
    }

    private static bool IsValidPassword(string? password)
    {
        if (password is null) return false;
        return Regex.IsMatch(password,
            @"^(?=(.*[A-Z]){2})(?=(.*\d){2})(?=(.*[a-z]){2})(?=(.*[!@#$%^&*()_+|\\/?<>:;'""-]){2})[A-Za-z\d!@#$%^&*()_+|\\/?<>:;'""-]*$");
    }
}