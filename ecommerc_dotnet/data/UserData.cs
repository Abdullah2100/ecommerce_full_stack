using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class UserData
{
    private readonly IConfigurationServices _configuration;
    private readonly AppDbContext _dbContext;
    // private readonly ILogger _logger;

    public UserData(AppDbContext dbContext,
        IConfigurationServices configuration

        // , ILogger logger
    )
    {
        _dbContext = dbContext;
        _configuration = configuration;
        // _logger = logger;
    }


    public async Task<UserInfoResponseDto?> getUser(string userName, string password)
    {
        try
        {
            var address = await _dbContext.Address
                .AsNoTracking()
                .Where(ad => ad.owner != null && ad.owner.name == userName && ad.owner.password == password)
                .OrderByDescending(ad => ad.created_at)
                .Select(ad => new AddressResponseDto()
                {
                    id = ad.id,
                    latitude = ad.latitude,
                    longitude = ad.longitude,
                }).ToListAsync() ?? null;

            var result = _dbContext.Users
                .AsNoTracking()
                .Where(u => ( u.email == userName) && u.password == password)
                .Select(u => new UserInfoResponseDto
                {
                    Id = u.ID,
                    name = u.name,
                    phone = u.phone,
                    email = u.email,
                    thumbnail = _configuration.getKey("url_file") + u.thumbnail,
                    address = address
                });

            return await result.FirstOrDefaultAsync();
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<UserInfoResponseDto?> getUser(Guid userID)
    {
        try
        {
            var address = await _dbContext.Address
                .AsNoTracking()
                .Where(ad => ad.owner_id == userID)
                .OrderByDescending(ad => ad.created_at)
                .Select(ad => new AddressResponseDto()
                {
                    id = ad.id,
                    latitude = ad.latitude,
                    longitude = ad.longitude,
                }).ToListAsync();

            var result = _dbContext.Users
                .AsNoTracking()
                .Where(u => u.ID == userID)
                .Select(u => new UserInfoResponseDto
                {
                    Id = u.ID,
                    name = u.name,
                    phone = u.phone,
                    email = u.email,
                    thumbnail = _configuration.getKey("url_file") + u.thumbnail,
                    address = address
                });

            return await result.FirstOrDefaultAsync();
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<User?> getUserById(Guid userID)
    {
        try
        {
            var user = await _dbContext.Users.FindAsync(userID);
            return user;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }


    public bool isExistByEmail(string email)
    {
        try
        {
            return _dbContext.Users?.AsNoTracking()?.Where(u => u.email == email && u.isDeleted == false) != null;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }

    
    public bool isExistByPhone(string phone)
    {
        try
        {
            return _dbContext.Users.FirstOrDefault(u => u.phone == phone && u.isDeleted == false) != null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
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

    public async Task<UserInfoResponseDto?> createNew(
        string name,
        string phone,
        string email,
        string password,
        int? role = 1
    )
    {
        try
        {
            if (role == 0)
            {
                var result = _dbContext.Users.FirstOrDefault(u => u.role == 1);
                if (result != null)
                {
                    Console.WriteLine("can't create new admin while there is already an admin");
                    return null;
                }
            }

            User userData = new User
            {
                name = name,
                email = email,
                phone = phone,
                password = password,
                created_at = DateTime.Now,
                ID = clsUtil.generateGuid(),
                updated_at = null
            };

            await _dbContext.Users.AddAsync(userData);
            await _dbContext.SaveChangesAsync();
            return await getUser(userData.ID);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User" + e.Message);
            return null;
        }
    }


    public async Task<UserInfoResponseDto?> updateUser(
        Guid userId,
        string? phone = null,
        string? password = null,
        string? name = null, 
        string? imagePath = null)
    {
        try
        {
            User? userData = _dbContext.Users.FirstOrDefault(u => u.ID == userId);

            if (userData == null)
                return null;

            userData.name = name ?? userData.name;
            userData.phone = phone ?? userData.phone;
            userData.password = password ?? userData.password;
            userData.updated_at = DateTime.Now;
            userData.thumbnail = imagePath;


            await _dbContext.SaveChangesAsync();
            return await getUser(userID: userData.ID);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from update User" + e.Message);
            return null;
        }
    }
}