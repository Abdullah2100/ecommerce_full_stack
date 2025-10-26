using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application;

public class ConfigurationImplement(IConfiguration configurationService) : IConfig
{

        private readonly IConfiguration? _configurationService = configurationService;

        public string getKey(string key)
        {
            string result = "";
            if (_configurationService !=  null)
            {
                result = _configurationService[key]!;
            }
            return result;
        }
        

}