using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.module;
using hotel_api.util;

namespace ecommerc_dotnet.data;

public class UserData
{
    private readonly AppDbContext _dbContext;
    // private readonly ILogger _logger;

    public UserData(AppDbContext dbContext
        // , ILogger logger
    )
    {
        _dbContext = dbContext;
        // _logger = logger;
    }

    public async Task<User?> createNew(SignupDto data)
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

            User userData = new User();
            userData.name = data.name;
            userData.email = data.email;
            userData.phone = data.phone;
            userData.ID = clsUtil.generateGuid();
            userData.username = data.username;
            userData.password = clsUtil.hashingText(data.password);
            userData.created_at = DateTime.Now;
            userData.updated_at = null;
            _dbContext.Users.Add(userData);
            await _dbContext.SaveChangesAsync();
            return userData;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User" + e.Message);
            return null;
        }
    }

    public User? getUser(string userName, string password)
    {
        try
        {
            return _dbContext.Users.FirstOrDefault(u =>
                (u.username == userName|| u.email==userName) && u.password == password && u.isDeleted == false);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public User? getUser(Guid userID)
    {
        try
        {
            return _dbContext.Users.FirstOrDefault(u => u.ID == userID && u.isDeleted == false);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<bool> deleteUser(Guid userID)
    {
        try
        {
            var result = _dbContext.Users.FirstOrDefault(u => u.ID == userID);
            result!.isDeleted = true;
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from delete user" + e.Message);
            return false;
        }
    }


    public async Task<User?> updateUser(UpdateUserInfo data, string? imagePath)
    {
        try
        {
            User? userData = _dbContext.Users.FirstOrDefault(u => u.ID == data.userId);

            if (userData == null)
                return null;

            userData.name = data.name ?? userData.name;
            userData.phone = data.phone ?? userData.phone;
            userData.username = data.username ?? userData.username;
            userData.password = clsUtil.hashingText(data.newPassword);
            userData.updated_at = DateTime.Now;
            userData.thumbnail = imagePath;

            if (data.address != null)
            {
                Address address = new Address();
                address.id = clsUtil.generateGuid();
                address.location = data.address;
                address.owner_id = userData.ID;
                userData.addresses.Add(address);
            }

            _dbContext.Users.Add(userData);
            await _dbContext.SaveChangesAsync();
            return userData;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User" + e.Message);
            return null;
        }
    }
}