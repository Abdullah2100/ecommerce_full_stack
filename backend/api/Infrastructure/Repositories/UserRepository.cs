using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.domain.entity;
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

    public async Task<List<User>> getUsers(int page, int length)
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

  
    public void  add(User entity)
    {
         dbContext.Users.Add(entity);
    }

    public void  update(User entity)
    {
         dbContext.Users.Update(entity);
    }

    public void  deleteAsync(Guid id)
    {
        User? user =  dbContext.Users.Find(id);
        if (user == null) throw new ArgumentNullException();
        user.IsBlocked = true;
    }

    public async Task<bool> isExist(Guid id)
    {
        return await dbContext.Users.FindAsync(id) != null;
    }
}