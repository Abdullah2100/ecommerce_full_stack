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
       
        
        // public static   void saveImage(
        //     string? imagePath,
        //     Guid? id
        //     , ImageBuissness? imageHolder = null
        // )
        // {
        //     if (imageHolder != null)
        //     {
        //         imageHolder.path = imagePath;
        //         imageHolder.save();
        //     }
        //     else if (imagePath != null && id != null)
        //     {
        //         imageHolder =
        //             new ImageBuissness(
        //                 new ImagesTbDto(
        //                     imagePath: imagePath,
        //                     belongTo: (Guid)id,
        //                     imagePathId: null,
        //                     isThumnail: false));
        //         imageHolder.save();
        //     }
        // }
        //
        // public static void saveImage(
        //     List<ImageRequestDto>? imagePath,
        //     Guid id
        // )
        // {
        //     if (imagePath != null)
        //     {
        //         foreach (var path in imagePath)
        //         {
        //             var imageHolder = new ImageBuissness(
        //                 new ImagesTbDto(
        //                     imagePath: path.fileName,
        //                     belongTo: id,
        //                     imagePathId: null,
        //                     isThumnail: path.isThumnail)
        //             );
        //             imageHolder.save();
        //         }
        //     }
        // }
    }
}