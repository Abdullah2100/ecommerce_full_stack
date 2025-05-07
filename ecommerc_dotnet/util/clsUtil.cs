using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using hotel_api.Services;

namespace hotel_api.util
{
    sealed class clsUtil
    {
        public enum enImageType
        {
            PROFILE,PRODUCT
        };
        private static string localPath = "images/";
        public static Guid generateGuid()
        {
            return Guid.NewGuid();
        }

        public static DateTime generateDateTime(AuthinticationServices.enTokenMode mode)
        {
            switch (mode)
            {
                case AuthinticationServices.enTokenMode.AccessToken:
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
            if (text == null) return "";
            using (SHA256 sha256 = SHA256.Create())
            {
                // Compute the hash of the given string
                byte[] hashValue = sha256.ComputeHash(Encoding.UTF8.GetBytes(text));
 
                // Convert the byte array to string format
                return BitConverter.ToString(hashValue).Replace("-", "");
            } 
        }
        
        
        public static string getFileExtention(string filename){
            return new FileInfo(filename).Extension;
        }

        private static bool createDirectory(string dir)
        {
            try
            {
                File.Create(dir);
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from creating file to save image on it "+ex.Message);
                return false;
            }
        }

        public static async Task<string?>saveFile(IFormFile file,enImageType type)
        {
            string filePath = clsUtil.localPath + type.ToString()+'/';
            string? fileFullName = null;
            try
            {
                if (!System.IO.File.Exists(filePath))
                {
                    if (!createDirectory(filePath))
                    {
                        return null;
                    }
                    
                }
                fileFullName = filePath+generateGuid()+getFileExtention(file.Name);

                using (var stream = new FileStream(fileFullName, FileMode.Create))
                {
                    await file.CopyToAsync(stream);
                }
                return fileFullName;

            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from saving image to local"+ex.Message);
                return null;
            }
        } 
        
      
    }
}