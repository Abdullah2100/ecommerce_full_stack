using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using ecommerc_dotnet.dto.Request;
using hotel_api.Services;

namespace hotel_api.util
{
    sealed class clsUtil
    {
        public enum enImageType
        {
            PROFILE,PRODUCT,CATEGORY,STORE,
            BANNER
        };
        private static string localPath = "/images/";
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
        
        
        public static string getFileExtention(IFormFile filename){
            return Path.GetExtension(filename.FileName);
        }

        private static bool createDirectory(string dir)
        {
            try
            {
                Directory.CreateDirectory(dir);
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from creating file to save image on it "+ex.Message);
                return false;
            }
        }

       public static async Task<string?>saveFile(IFormFile file,enImageType type,IWebHostEnvironment host)
        {
            string filePath = localPath + type.ToString()+"/";
            
            string? fileFullName = null;
            try
            {
                if (!Directory.Exists(host.ContentRootPath+filePath))
                {
                    if (!createDirectory(host.ContentRootPath+filePath))
                    {
                        return null;
                    }
                    
                }
                fileFullName = filePath+generateGuid()+getFileExtention(file);

                using (var stream = new FileStream(host.ContentRootPath+fileFullName, FileMode.Create))
                {
                    await file.CopyToAsync(stream);
                }
                return fileFullName.Replace("/images","");

            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from saving image to local"+ex.Message);
                return null;
            }
        } 
        
       
        public static async Task<List<string>?>saveFile(List<IFormFile> file,enImageType type,IWebHostEnvironment host)
        {
            List<string> images = new List<string>();
            foreach (var image in file)
            {
               var path = await saveFile(image,type,host);
               if (path == null)
               {
                   deleteFile(images, host);
                   return null;
               }
               
               images.Add(path);
            }

            return images;
        } 


        public static bool deleteFile(string filePath,IWebHostEnvironment host,string? addtionUrl=null)
        {
            try
            {
                var fileRealPath = addtionUrl!=null?filePath.Replace(addtionUrl,""):filePath;
                if (File.Exists(host.ContentRootPath+"/images"+fileRealPath))
                {
                    File.Delete(host.ContentRootPath +"/images"+ fileRealPath);
                    return true;
                }

                return false;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from delete image  "+ex.Message);
                return false;
            }
        } 

        public static void deleteFile(List<string> filePath,IWebHostEnvironment host,string? addtionUrl=null)
        {
             filePath.ForEach(image=>deleteFile(image,host,addtionUrl)); 
        } 
 
    }
}