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
                .Where(u => (u.email == userName) && u.password == password)
                .Select(u => new UserInfoResponseDto
                {
                    Id = u.id,
                    name = u.name,
                    phone = u.phone,
                    email = u.email,
                    thumbnail = String.Empty,
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
                .Include(u => u.store)
                .AsSplitQuery()
                .Where(us => us.id == userID)
                .AsNoTracking()
                .Select(us => new UserInfoResponseDto
                {
                    name = us.name,
                    phone = us.phone,
                    email = us.email,
                    Id = us.id,
                    thumbnail = us.thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.thumbnail,

                    address = null,
                    storeId = us.store == null ? null : us!.store.id
                }).FirstOrDefaultAsync();

            if (result != null)
            {
                //result.address =await _dbContext.Address.Where(ad=>ad.ownerId==userID).ToListAsync()
                result.address = await _dbContext.Address
                    .Where(ad => ad.ownerId == userID)
                    .AsNoTracking()
                    .Select(ad => new AddressResponseDto
                    {
                        id = ad.id,
                        longitude = ad.longitude,
                        latitude = ad.latitude,
                        title = ad.title,
                        isCurrent = ad.isCurrent,
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
                .Include(u => u.store)
                .AsSplitQuery()
                .Where(us => us.email == email)
                .AsNoTracking()
                .Select(us => new UserInfoResponseDto
                {
                    name = us.name,
                    phone = us.phone,
                    email = us.email,
                    Id = us.id,
                    thumbnail = us.thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.thumbnail,

                    address = null,
                    storeId = us.store == null ? null : us!.store.id
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
                .Include(u => u.store)
                .AsSplitQuery()
                .AsNoTracking()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(us => new UserInfoResponseDto
                {
                    name = us.name,
                    phone = us.phone,
                    email = us.email,
                    Id = us.id,
                    storeName = us.store != null ? us.store.name : "",
                    isAdmin = us.role == 0,
                    isActive = us.isDeleted,
                    thumbnail = us.thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.thumbnail,

                    address = null,
                    storeId = us.store == null ? null : us!.store.id
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
                .Include(st => st.store)
                .AsSplitQuery()
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.id == ID);
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
                .Include(st => st.store)
                .AsSplitQuery()
                .AsNoTracking()
                .FirstOrDefaultAsync(u => u.store != null && u.store.id == storeId);
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
                .FirstOrDefaultAsync(u => u.id == userid && u.isDeleted == false) != null;
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
                .FirstOrDefaultAsync(u => u.email == email) != null;
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
                .FirstOrDefaultAsync(u => u.phone == phone && u.isDeleted == false) != null;
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
            result.isDeleted = !result.isDeleted;
            await _dbContext.SaveChangesAsync();
            return result.isDeleted;
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
                    .FirstOrDefaultAsync(u => u.role == 1);
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
                createdAt = DateTime.Now,
                id = clsUtil.generateGuid(),
                updatedAt = null,
                deviceToken = deviceToken ?? ""
            };

            await _dbContext
                .Users.AddAsync(userData);
            await _dbContext.SaveChangesAsync();
            return await getUser(userData.id);
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

            userData.name = name ?? userData.name;
            userData.phone = phone ?? userData.phone;
            userData.password = password ?? userData.password;
            userData.updatedAt = DateTime.Now;
            userData.thumbnail = imagePath ?? userData.thumbnail;


            await _dbContext.SaveChangesAsync();
            return await getUser(userID: userData.id);
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
            return await getUser(userID: userData.id);
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
               .Where(u => u.email == email)
               .ExecuteUpdateAsync(
                   us => us
                       .SetProperty(value => value.password, newPassword));

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