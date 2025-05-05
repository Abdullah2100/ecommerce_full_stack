using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.module;
using hotel_api.util;

namespace ecommerc_dotnet.data;

public class UserData
{
   private readonly AppDbContext _dbContext;
   // private readonly ILogger _logger;

 public   UserData(AppDbContext dbContext
     // , ILogger logger
     )
    {
        _dbContext = dbContext;
        // _logger = logger;
    }

    public  async Task<User?> createNew(SignupDto data)
    {
        try
        {
            if (data.role == 0)
            {
               var result = _dbContext.Users.FirstOrDefault(u => u.role == 1);
               if (result != null)
               {
                   Console.WriteLine("can't create new admin while there is already an admin");
                   return null;
               }
            }
           
            Person person = new Person();
            person.ID = clsUtil.generateGuid();
            person.name = data.name;
            person.email = data.email;
            person.phone = data.phone;
            person.address = data.address;
            User userData = new User();
            userData.ID = clsUtil.generateGuid();
            userData.username = data.username;
            userData.password = clsUtil.hashingText(data.password);
            userData.created_at = DateTime.Now;
            userData.person = person;
            
            _dbContext.Users.Add(userData);
           await _dbContext.SaveChangesAsync();
           return  userData;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User"+e.Message);
            return null;
        }
    }

    public User? getUser(string userName , string password)
    {
        try
        {
            return  _dbContext.Users.FirstOrDefault(u => u.username == userName && u.password ==password && u.isDeleted==false);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username"+e.Message);
            return null;
        }
    }
    
    public User? getUser(Guid userID)
    {
        try
        {
            return  _dbContext.Users.FirstOrDefault(u => u.ID==userID && u.isDeleted==false);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username"+e.Message);
            return null;
        }
    }
    
    public async Task<bool> deleteUser(Guid userID)
    {
        try
        {
            var result =  _dbContext.Users.FirstOrDefault(u => u.ID==userID);
            result!.isDeleted = true;
         await   _dbContext.SaveChangesAsync();
         return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from delete user"+e.Message);
            return false;
        }
    }



    
}