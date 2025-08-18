using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.module;
using Google;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ReseatePasswordRepository(AppDbContext context) : IReseatePasswordRepository
{
    public async Task<IEnumerable<ReseatePasswordOtp>> getAllAsync(int page, int length)
    {
        return await context
            .ReseatPasswords
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public async Task<int> addAsync(ReseatePasswordOtp entity)
    {
        await context.ReseatPasswords.AddAsync(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(ReseatePasswordOtp entity)
    {
        context.ReseatPasswords.Update(entity);
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        ReseatePasswordOtp? entity = await context.ReseatPasswords.FindAsync(id);
        if (entity == null) return 0;
        await context.ReseatPasswords
            .Where(f => f.Email == entity.Email)
            .ExecuteDeleteAsync();
        return 1;
    }


    public async Task<ReseatePasswordOtp?> getOtp(string otp)
    {
        return await context.ReseatPasswords
            .AsNoTracking()
            .FirstOrDefaultAsync(f => f.Otp == otp);
    }

    private async Task deleteAllEmailOtp(string email, string otp)
    {
        await context.ReseatPasswords.Where(rp => rp.Email == email && rp.Otp != otp).ExecuteDeleteAsync();
    }

    public async Task<bool> isExist(string otp, string email)
    {
        //delete previus otp for email
        await deleteAllEmailOtp(email, otp);
        
        ReseatePasswordOtp? passwordOtp = await getOtp(otp);
        if (passwordOtp is null) return false;
        return true;
    }

    public async Task<ReseatePasswordOtp?> getOtp(string otp, string email, bool state)
    {
        var otpHolder= await context.ReseatPasswords
            .AsNoTracking()
            .FirstOrDefaultAsync(f => f.Otp == otp && f.Email == email && f.IsValidated == state);
        if (otpHolder is null) return null;

        
        if (otpHolder.CreatedAt > DateTime.Now)
        {
            return otpHolder;
        }

        return null;
    }
}