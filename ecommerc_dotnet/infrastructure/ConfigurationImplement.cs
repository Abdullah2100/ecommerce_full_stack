namespace ecommerc_dotnet.midleware.ConfigImplment;

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