using ecommerc_dotnet.application.Interface;
using hotel_api.util;

namespace ecommerc_dotnet.application.Services
{


    public class FileServices(IWebHostEnvironment host) : IFileServices
    {
        private static string localPath = "images";

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

        public async Task<string?> saveFile(IFormFile file, EnImageType type)
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

                fileFullName = Path.Combine(filePath, clsUtil.generateGuid() + getFileExtention(file));

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

        public async Task<List<string>?> saveFile(List<IFormFile> file, EnImageType type)
        {
            List<string> images = new List<string>();
            foreach (var image in file)
            {
                string? path = await saveFile(image, type);
                if (path is null)
                {
                    deleteFile(images);
                    return null;
                }

                images.Add(path);
            }

            return images;
        }

        public bool deleteFile(string filePath)
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

        public bool deleteFile(List<string> filePaths)
        {
            try
            {
                foreach (var filePath in filePaths)
                {
                    deleteFile(filePath);
                }

                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from delete image  " + ex.Message);
                return false;
            }
        }
    }
    
}