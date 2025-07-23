using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class UserData
{
    private readonly IConfig _configuration;
    private readonly AppDbContext _dbContext;

    public UserData(AppDbContext appDbContext)
    {
        _dbContext = appDbContext;
    }

    public UserData(AppDbContext dbContext,
        IConfig configuration
    )
    {
        _dbContext = dbContext;
        _configuration = configuration;
    }


    public async Task<UserInfoResponseDto?> getUser(string userName, string password)
    {
        try
        {
            UserInfoResponseDto? result = await _dbContext.Users
                .AsNoTracking()
                .Where(u => (u.Email == userName) && u.Password == password)
                .Select(u => new UserInfoResponseDto
                {
                    Id = u.Id,
                    Name = u.Name,
                    Phone = u.Phone,
                    Email = u.Email,
                    Thumbnail = String.Empty,
                })
                .FirstOrDefaultAsync();
            if (result is null) return null;


            return result;
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
            UserInfoResponseDto? result = await _dbContext.Users
                .Include(u => u.Store)
                .AsSplitQuery()
                .Where(us => us.Id == userID)
                .AsNoTracking()
                .Select(us => new UserInfoResponseDto
                {
                    Name = us.Name,
                    Phone = us.Phone,
                    Email = us.Email,
                    Id = us.Id,
                    Thumbnail = us.Thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.Thumbnail,

                    address = null,
                    storeId = us.Store == null ? null : us!.Store.Id
                }).FirstOrDefaultAsync();

            if (result != null)
            {
                //result.address =await _dbContext.Address.Where(ad=>ad.ownerId==userID).ToListAsync()
                result.address = await _dbContext.Address
                    .Where(ad => ad.OwnerId == userID)
                    .AsNoTracking()
                    .Select(ad => new AddressResponseDto
                    {
                        id = ad.Id,
                        longitude = ad.Longitude,
                        latitude = ad.Latitude,
                        title = ad.Title,
                        isCurrent = ad.IsCurrent,
                    }).ToListAsync();
            }

            return result;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<UserInfoResponseDto?> getUser(string email)
    {
        try
        {
            UserInfoResponseDto? result = await _dbContext.Users
                .Include(u => u.Store)
                .AsSplitQuery()
                .Where(us => us.Email == email)
                .AsNoTracking()
                .Select(us => new UserInfoResponseDto
                {
                    Name = us.Name,
                    Phone = us.Phone,
                    Email = us.Email,
                    Id = us.Id,
                    Thumbnail = us.Thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.Thumbnail,

                    address = null,
                    storeId = us.Store == null ? null : us!.Store.Id
                }).FirstOrDefaultAsync();

            return result;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<List<UserInfoResponseDto>?> getUsers(int pageNumber, int pageSize = 25)
    {
        try
        {
            return await _dbContext.Users
                .Include(u => u.Store)
                .AsSplitQuery()
                .AsNoTracking()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(us => new UserInfoResponseDto
                {
                    Name = us.Name,
                    Phone = us.Phone,
                    Email = us.Email,
                    Id = us.Id,
                    StoreName = us.Store != null ? us.Store.Name : "",
                    IsAdmin = us.Role == 0,
                    IsActive = us.IsBlocked,
                    Thumbnail = us.Thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.Thumbnail,

                    address = null,
                    storeId = us.Store == null ? null : us!.Store.Id
                })
                .ToListAsync();
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }


    public async Task<int> getUsers()
    {
        try
        {
            return await _dbContext
                .Users
                .AsNoTracking()
                .CountAsync();
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return 0;
        }
    }


    public async Task<User?> getUserById(Guid ID)
    {
        try
        {
            return await _dbContext.Users
                .Include(st => st.Store)
                .AsSplitQuery()
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Id == ID);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }

    public async Task<User?> getUserByStoreId(Guid storeId)
    {
        try
        {
            return await _dbContext.Users
                .Include(st => st.Store)
                .AsSplitQuery()
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Store != null && u.Store.Id == storeId);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }
    public async Task<bool> isExist(Guid userid)
    {
        try
        {
            return await _dbContext.Users
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Id == userid && u.IsBlocked == false) != null;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }


    public async Task<bool> isExistByEmail(string email)
    {
        try
        {
            return await _dbContext.Users
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Email == email) != null;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }


    public async Task<bool> isExistByPhone(string phone)
    {
        try
        {
            return await _dbContext.Users
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.Phone == phone && u.IsBlocked == false) != null;
        }
        catch (Exception e)
        {
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }

    public async Task<bool?> deleteUser(Guid userID)
    {
        try
        {
            var result = await _dbContext.Users
                  .FindAsync(userID);

            if (result is null) return null;
            result.IsBlocked = !result.IsBlocked;
            await _dbContext.SaveChangesAsync();
            return result.IsBlocked;
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
        string? deviceToken = null,
        int? role = 1
    )
    {
        try
        {
            if (role == 0)
            {
                User? result = await _dbContext.Users
                    .FirstOrDefaultAsync(u => u.Role == 1);
                if (result != null)
                {
                    Console.WriteLine("can't create new admin while there is already an admin");
                    return null;
                }
            }

            User userData = new User
            {
                Name = name,
                Email = email,
                Phone = phone,
                Password = password,
                CreatedAt = DateTime.Now,
                Id = clsUtil.generateGuid(),
                UpdatedAt = null,
                deviceToken = deviceToken ?? ""
            };

            await _dbContext
                .Users.AddAsync(userData);
            await _dbContext.SaveChangesAsync();
            return await getUser(userData.Id);
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
            User? userData = await _dbContext.Users
                .FindAsync(userId);

            if (userData is null)
                return null;

            userData.Name = name ?? userData.Name;
            userData.Phone = phone ?? userData.Phone;
            userData.Password = password ?? userData.Password;
            userData.UpdatedAt = DateTime.Now;
            userData.Thumbnail = imagePath ?? userData.Thumbnail;


            await _dbContext.SaveChangesAsync();
            return await getUser(userID: userData.Id);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from update User" + e.Message);
            return null;
        }
    }

    public async Task<UserInfoResponseDto?> updateUserDeviceToken(
        Guid userId,
        string? deviceToken)
    {
        try
        {
            User? userData = await _dbContext.Users
                .FindAsync(userId);

            if (userData is null)
                return null;

            userData.deviceToken = deviceToken ?? userData.deviceToken;


            await _dbContext.SaveChangesAsync();
            return await getUser(userID: userData.Id);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from update User" + e.Message);
            return null;
        }
    }

    public async Task<UserInfoResponseDto?> updateUserPassword(
          string email,
          string newPassword)
    {
        try
        {
            await _dbContext.Users
               .Where(u => u.Email == email)
               .ExecuteUpdateAsync(
                   us => us
                       .SetProperty(value => value.Password, newPassword));

            await _dbContext.SaveChangesAsync();
            return await getUser(email: email);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from update User" + e.Message);
            return null;
        }
    }


}