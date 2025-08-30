using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using ecommerc_dotnet.dto.Request;
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
        private static string localPath = "images";

        public static Guid generateGuid()=> Guid.NewGuid();

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


        public static string getFileExtention(IFormFile filename)=> Path.GetExtension(filename.FileName);
        

        private static bool createDirectory(string dir)
        {
            try
            {
                Directory.CreateDirectory(dir);
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from creating file to save image on it " + ex.Message);
                return false;
            }
        }

        public static async Task<string?> saveFile(IFormFile file, EnImageType type, IWebHostEnvironment host)
        {
            // string filePath = localPath + type.ToString()+"/";
            string filePath = Path.Combine(host.ContentRootPath, localPath, type.ToString());
            string? fileFullName = null;
            try
            {
                if (!Directory.Exists(filePath))
                {
                    if (!createDirectory(filePath))
                    {
                        return null;
                    }
                }

                fileFullName = Path.Combine(filePath, generateGuid() + getFileExtention(file));

                using (var stream = new FileStream(fileFullName, FileMode.Create))
                {
                    await file.CopyToAsync(stream);
                }

                switch (fileFullName.Contains("//"))
                {
                    case true: return fileFullName.Replace(host.ContentRootPath + "//images", "");
                    default: return fileFullName.Replace(host.ContentRootPath + "/images", "");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from saving image to local" + ex.Message);
                return null;
            }
        }


        public static async Task<List<string>?> saveFile(List<IFormFile> file, EnImageType type,
            IWebHostEnvironment host)
        {
            List<string> images = new List<string>();
            foreach (var image in file)
            {
                string? path = await saveFile(image, type, host);
                if (path is null)
                {
                    deleteFile(images, host);
                    return null;
                }

                images.Add(path);
            }

            return images;
        }


        public static bool deleteFile(string filePath, IWebHostEnvironment host)
        {
            try
            {
                var newFilPath = filePath.Substring(1);
                string fileRealPath = Path.Combine(host.ContentRootPath,"images", newFilPath);
                if (File.Exists(fileRealPath))
                {
                    File.Delete(fileRealPath);
                    return true;
                }

                return false;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from delete image  " + ex.Message);
                return false;
            }
        }

        public static void deleteFile(List<string> filePath, IWebHostEnvironment host)=>  
            filePath.ForEach(image => { deleteFile(image, host); });
        
    }
}