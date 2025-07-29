using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Google;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ReseatePasswordRepository:IReseatePasswordRepository
{
    private readonly AppDbContext _context;

    public ReseatePasswordRepository(AppDbContext context)
    {
        _context = context;
    }
    
    public async Task<IEnumerable<ReseatePasswordOtp>> getAllAsync(int page, int length)
    {
        return await _context
            .ReseatPasswords
            .AsNoTracking()
            .Skip((page-1)*length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(ReseatePasswordOtp entity)
    {
    
      await _context.ReseatPasswords.AddAsync(entity);
      return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(ReseatePasswordOtp entity)
    {
         _context.ReseatPasswords.Update(entity);
        return await _context.SaveChangesAsync(); 
    }

    public async Task<int> deleteAsync(Guid id)
    {
        ReseatePasswordOtp?  entity = await _context.ReseatPasswords.FindAsync(id);
        if (entity == null) return 0;
        await  _context.ReseatPasswords
            .Where(f => f.Email == entity.Email)
            .ExecuteDeleteAsync();
        return await _context.SaveChangesAsync(); 
    }



    public async Task<ReseatePasswordOtp?> getOtp(string otp)
    {
        return await _context.ReseatPasswords
            .AsNoTracking()
            .FirstOrDefaultAsync(f => f.Otp == otp);
    }
    
    public async Task<bool> isExist(string otp)
    {
        ReseatePasswordOtp? passwordOtp = await getOtp(otp);
        if (passwordOtp != null)
        {
           await deleteAsync(passwordOtp.Id);
        }
        return await _context.ReseatPasswords
            .AsNoTracking()
            .AnyAsync(u =>
                (u.Otp == otp) && (u.CreatedAt.AddHours(1).Microsecond > DateTime.Now.Microsecond));

    }
    
    public async Task<ReseatePasswordOtp?> getOtp(string otp, string email)
    {
        return await _context.ReseatPasswords
            .AsNoTracking()
            .FirstOrDefaultAsync(f => f.Otp == otp && f.Email == email && f.IsValidated == false);

    }
}