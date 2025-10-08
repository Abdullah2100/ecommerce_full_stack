using hotel_api.util;

namespace ecommerc_dotnet.application.Interface;


public interface IFileServices
{
    Task<string?> saveFile(IFormFile file, EnImageType type);
    Task<List<string>?> saveFile(List<IFormFile> file, EnImageType type);

    bool deleteFile(string filePath);
    bool deleteFile(List<string> filePaths);
}