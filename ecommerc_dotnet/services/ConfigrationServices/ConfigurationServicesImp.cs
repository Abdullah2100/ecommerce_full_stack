
namespace hotel_api.Services
{
    public class ConfigurationServicesImp : IConfigurationServices
    {

private readonly IConfiguration? _configurationService;

        public ConfigurationServicesImp(IConfiguration configurationService){
            _configurationService = configurationService;
        }

        public string getKey(string key)
        {
            string result = "";
            if (_configurationService != null)
            {
                result = _configurationService[key]!;
            }
            return result;
        }

        
    }

  
}