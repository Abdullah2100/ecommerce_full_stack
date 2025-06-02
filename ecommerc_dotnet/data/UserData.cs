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
    // private readonly ILogger _logger;

    public UserData(AppDbContext appDbContext)
    {
        _dbContext = appDbContext;
    }
    public UserData(AppDbContext dbContext,
        IConfig configuration

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
            var result = await _dbContext.Users
                .AsNoTracking()
                .Where(u => (u.email == userName) && u.password == password)
                .Select(u => new UserInfoResponseDto
                {
                    Id = u.ID,
                    name = u.name,
                    phone = u.phone,
                    email = u.email,
                    thumbnail = _configuration.getKey("url_file") + u.thumbnail,
                    address = null,
                    store_id = u.Store.id
                })
                .FirstOrDefaultAsync();
            
            var address = await _dbContext.Address
                .AsNoTracking()
                .Where(ad => ad.owner_id == result.Id)
                .OrderByDescending(ad => ad.created_at)
                .Select(ad => new AddressResponseDto()
                {
                    id = ad.id,
                    latitude = ad.latitude,
                    longitude = ad.longitude,
                    title = ad.title
                }).ToListAsync() ?? null;

            result.address = address;

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
            var result =await _dbContext.Users
                .AsNoTracking()
                .Include(u=>u.Store)
                .Where(us => us.ID == userID)
                .Select(us => new UserInfoResponseDto
                {
                    name = us.name,
                    phone = us.phone,
                    email = us.phone,
                    Id = us.ID,
                    thumbnail = us.thumbnail == null
                        ? ""
                        : _configuration.getKey("url_file") + us.thumbnail,

                    address = null,
                    store_id =us.Store==null?null: us!.Store.id
                }).FirstOrDefaultAsync();
            
            if (result != null) {
                //result.address =await _dbContext.Address.Where(ad=>ad.owner_id==userID).ToListAsync()
                result.address =await _dbContext.Address
                    .AsNoTracking()
                    .Where(ad => ad.owner_id == userID)
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
    
    public async Task<User?> getUserById(Guid ID)
    {
        try
        {
            return await _dbContext.Users
                .Include(st=>st.Store)
                .FirstOrDefaultAsync(u => u.ID == ID);
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
                .FirstOrDefaultAsync(u => u.ID==userid&&u.isDeleted==false) != null;
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

    public async Task<bool> deleteUser(Guid userID)
    {
        try
        {
            var result = _dbContext.Users
                .AsNoTracking()
                .FirstOrDefault(u => u.ID == userID);
            
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
        string? deviceToken=null,
        int? role = 1
    )
    {
        try
        {
            if (role == 0)
            {
                var result = await _dbContext.Users
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
                created_at = DateTime.Now,
                ID = clsUtil.generateGuid(),
                updated_at = null,
                deviceToken = deviceToken??""
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
            User? userData = await _dbContext.Users
                .FirstOrDefaultAsync(u => u.ID == userId);

            if (userData == null)
                return null;

            userData.name = name ?? userData.name;
            userData.phone = phone ?? userData.phone;
            userData.password = password ?? userData.password;
            userData.updated_at = DateTime.Now;
            userData.thumbnail = imagePath ?? userData.thumbnail;


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
    public async Task<UserInfoResponseDto?> updateUserDeviceToken(
        Guid userId,
        string? deviceToken )
    {
        try
        {
            User? userData = await _dbContext.Users
                .FirstOrDefaultAsync(u => u.ID == userId);

            if (userData == null)
                return null;

            userData.deviceToken = deviceToken??userData.deviceToken;


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