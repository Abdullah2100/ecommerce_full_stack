using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ReseatPasswordRepository(AppDbContext context) : IReseatePasswordRepository
{
    public async Task<IEnumerable<ReseatPasswordOtp>> GetAllAsync(int page, int length)
    {
        return await context
            .ReseatPasswords
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .ToListAsync();
    }

    public void Add(ReseatPasswordOtp entity)
    {
         context.ReseatPasswords.Add(entity);
    }

    public void  Update(ReseatPasswordOtp entity)
    {
        context.ReseatPasswords.Update(entity);
    }

    public void  Delete(Guid id)
    {
        ReseatPasswordOtp? entity =  context.ReseatPasswords.Find(id);
        if (entity == null) throw new ArgumentNullException();
        var previousPassword = context.ReseatPasswords
            .Where(f => f.Email == entity.Email)
            .ToList();
        context.ReseatPasswords.RemoveRange(previousPassword!);
    }


    public async Task<ReseatPasswordOtp?> GetOtp(string otp)
    {
        return await context.ReseatPasswords
            .AsNoTracking()
            .FirstOrDefaultAsync(f => f.Otp == otp);
    }

    private async Task DeleteAllEmailOtp(string email, string otp)
    {
        await context.ReseatPasswords.Where(rp => rp.Email == email && rp.Otp != otp).ExecuteDeleteAsync();
    }

    public async Task<bool> IsExist(string otp, string email)
    {
        //delete previus otp for email
        await DeleteAllEmailOtp(email, otp);
        
        ReseatPasswordOtp? passwordOtp = await GetOtp(otp);
        if (passwordOtp is null) return false;
        return true;
    }

    public async Task<ReseatPasswordOtp?> GetOtp(string otp, string email, bool state)
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