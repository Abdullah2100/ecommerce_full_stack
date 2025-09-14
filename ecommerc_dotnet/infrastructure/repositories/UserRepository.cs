using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class UserRepository(AppDbContext dbContext) : IUserRepository
{
    public async Task<User?> getUser(Guid id)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Id == id);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .ToListAsync();

        return user;
    }

    public async Task<User?> getUser(string email)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsNoTracking()
            .FirstOrDefaultAsync(u =>  u.Email == email);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == user.Id)
            .ToListAsync();

        return user;
    }

    public async Task<int> getUserCount()
    {
        return await dbContext
            .Users
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<int> getUserAddressCount(Guid id)
    {
        return await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .CountAsync();
    }

    public async Task<User?> getUserByStoreId(Guid id)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Store != null && u.Store.Id == id);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .ToListAsync();

        return user;
    }

    public async Task<User?> getUser(string username, string password)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsNoTracking()
            .FirstOrDefaultAsync(u => (u.Name == username||u.Email==username) && u.Password == password);
       
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == user.Id)
            .ToListAsync();

        return user;
    }

    public async Task<bool> isExist(int role)
    {
        return await dbContext
            .Users
            .AsNoTracking()
            .AnyAsync(u => u.Role == role);
    }

    public async Task<bool> isExistByPhone(string phone)
    {
        return (await dbContext
                .Users
                .AsNoTracking()
                .AnyAsync(u => u.Phone == phone)
            );
    }

    public async Task<bool> isExistByEmail(string email)
    {
        return (await dbContext
                .Users
                .AsNoTracking()
                .AnyAsync(u => u.Email == email)
            );
    }

    public virtual async Task<IEnumerable<User>> getAllAsync(
        int page,
        int length
    )
    {
        List<User>? users = await dbContext
            .Users
            .Include(u => u.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .Skip((page - 1) * length)
            .OrderDescending()
            .Take(length)
            .ToListAsync();

        foreach (var user in users)
        {
            user.Addresses = await dbContext
                .Address
                .AsNoTracking()
                .Where(u => u.OwnerId == user.Id)
                .ToListAsync();
        }

        return users;
    }

    public async Task<int> addAsync(User entity)
    {
        await dbContext.Users.AddAsync(entity);
        return await dbContext.SaveChangesAsync();
    }

    public async Task<int> updateAsync(User entity)
    {
         dbContext.Users.Update(entity);
        return await dbContext.SaveChangesAsync();
    }

    public virtual async Task<int> deleteAsync(Guid id)
    {
        User? user = await dbContext.Users.FindAsync(id);
        if (user == null) return 0;
        user.IsBlocked = true;
       return await dbContext.SaveChangesAsync();
    }

    public async Task<bool> isExist(Guid id)
    {
        return await dbContext.Users.FindAsync(id) != null;
    }
}