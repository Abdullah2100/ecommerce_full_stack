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
            var result = _dbContext.Users
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
                    store = null
                }).FirstOrDefault();
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
            return null;
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
            return await (from us in _dbContext.Users
                    join st in _dbContext.Store on us.ID equals st.user_id
                    where us.ID == userID
                    select new UserInfoResponseDto
                    {
                        name = us.name,
                        phone = us.phone,
                        email = us.phone,
                        Id = us.ID,
                        thumbnail = st.user.thumbnail == null
                            ? null
                            : _configuration.getKey("url_file") + st.user.thumbnail,
                        address = _dbContext.Address.Where(add => add.owner_id == st.user_id)
                            .Select(add => new AddressResponseDto
                            {
                                id = add.id,
                                isCurrent = add.isCurrent,
                                latitude = add.latitude,
                                longitude = add.longitude,
                                title = add.title
                            }).ToList(),
                        store = new StoreResponseDto
                        {
                            id = st.id,
                            name = st.name,
                            wallpaper_image = _configuration.getKey("url_file") + st.wallpaper_image,
                            small_image = _configuration.getKey("url_file") + st.small_image,
                            created_at = st.created_at,
                            latitude = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                            longitide = (_dbContext.Address.FirstOrDefault(adh => adh.owner_id == st.id).latitude),
                            subcategory = st.SubCategories.Select(sub => new SubCategoryResponseDto
                            {
                                name = sub.name,
                                id = sub.id,
                            }).ToList(),
                            user = null
                        }
                    }
                )
                .AsNoTracking()
                .FirstOrDefaultAsync();
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return null;
        }
    }
    // public static UserInfoResponseDto? getUser(Guid userID,AppDbContext _dbContext,IConfigurationServices _configuration)
    //    {
    //        try
    //        {
    //            var result = _dbContext.Users
    //                .AsNoTracking()
    //                .Where(u => u.ID == userID)
    //                .Select(u => new UserInfoResponseDto
    //                {
    //                    Id = u.ID,
    //                    name = u.name,
    //                    phone = u.phone,
    //                    email = u.email,
    //                    thumbnail =u.thumbnail==null?"": _configuration.getKey("url_file") + u.thumbnail,
    //                    address =  AddressData.getUserAddressByUserId(userID, _dbContext),
    //                    store = null
    //                }).FirstOrDefault();
    //
    //            return  result; 
    //        }
    //        catch (Exception e)
    //        {
    //            //_logger.LogError("error from get user by username"+e.Message);
    //            Console.WriteLine("error from get user by username" + e.Message);
    //            return null;
    //        }
    //    }

    public async Task<User?> getUserById(Guid ID)
    {
        try
        {
            return _dbContext.Users
                .FirstOrDefault(u => u.ID == ID);
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
        int? role = 1
    )
    {
        try
        {
            if (role == 0)
            {
                var result = _dbContext.Users
                    .FirstOrDefault(u => u.role == 1);
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
}