using System.Text.RegularExpressions;

namespace hotel_api.util;

sealed class clsValidation
{
    public static string? validateInput(string? phone,
        string? email,
        string? password,
        bool? isNeedValidateOther=true,
        string username = "",
        string name = "")
    {
        if (phone !=  null && !isValidPhone(phone))
            return "ادخل رقم هاتف صحيح";
        if (email !=  null && !isValidEmail(email))
            return "ادخل ايميل صحيح";
        if (password !=  null && !isValidPassword(password))
            return "ادخل كلمة مرور مناسبة";
        if (isNeedValidateOther==true&&username.Length <= 0)
            return "اسم المستخدم لا يمكن ان يكون فارغا";
        if (isNeedValidateOther==true&&name.Length <= 0)
            return "الاسم لا يمكو ان يكون فارغا";
        return null;
    }
    
    public static string? validateInput(
        string? email=null,
        String? password=null,
        string? phone=null)
    {
        if (phone !=  null && !isValidPhone(phone))
            return "ادخل رقم هاتف صحيح";
        if (email !=  null && !isValidEmail(email))
            return "ادخل ايميل صحيح";
        if (password !=  null && !isValidPassword(password))
            return "ادخل كلمة مرور مناسبة";
       
        return null;
    }


    public static bool isValidPhone(string? phone)
    {
        if (phone is null) return false;
        return Regex.Match(phone, @"^\+?\d{9,15}$").Success;
    }

    public static bool isValidEmail(string? email)
    {
        if (email is null) return false;
        return Regex.Match(email, @"^[a-zA-Z0-9._%±]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$").Success;
    }

    public static bool isValidPassword(string? password)
    {
        if (password is null) return false;
        return Regex.IsMatch(password,
            @"^(?=(.*[A-Z]){2})(?=(.*\d){2})(?=(.*[a-z]){2})(?=(.*[!@#$%^&*()_+|\\/?<>:;'""-]){2})[A-Za-z\d!@#$%^&*()_+|\\/?<>:;'""-]*$");
    }
}