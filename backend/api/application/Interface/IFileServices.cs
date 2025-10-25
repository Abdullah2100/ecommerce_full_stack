using api.util;

namespace api.application.Interface;


public interface IFileServices
{
    Task<string?> SaveFile(IFormFile file, EnImageType type);
    Task<List<string>?> SaveFile(List<IFormFile> file, EnImageType type);

    bool DeleteFile(string filePath);
    bool DeleteFile(List<string> filePaths);
}