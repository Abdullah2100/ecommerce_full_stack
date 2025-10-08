using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using ecommerc_dotnet.Presentation.dto.Request;
using hotel_api.Services;

namespace hotel_api.util
{
    public enum EnImageType
    {
        PROFILE,
        PRODUCT,
        CATEGORY,
        STORE,
        BANNER,
        DELIVERY
    };

    static class clsUtil
    {
        public static Guid generateGuid() => Guid.NewGuid();

        public static DateTime generateDateTime(EnTokenMode mode)
        {
            switch (mode)
            {
                case EnTokenMode.AccessToken:
                {
                    return DateTime.Now.AddSeconds(40);
                }
                default:
                {
                    return DateTime.Now.AddDays(30);
                }
            }
        }


        public static string hashingText(string? text)
        {
            if (text is null) return "";
            using (SHA256 sha256 = SHA256.Create())
            {
                // Compute the hash of the given string
                byte[] hashValue = sha256.ComputeHash(Encoding.UTF8.GetBytes(text));

                // Convert the byte array to string format
                return BitConverter.ToString(hashValue).Replace("-", "");
            }
        }
    }
}