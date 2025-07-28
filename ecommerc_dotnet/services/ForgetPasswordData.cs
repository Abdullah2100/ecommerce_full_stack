using System.Threading.Tasks;
using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class ForgetPasswordData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;

    public ForgetPasswordData(
        AppDbContext dbContext,
        IUnitOfWork unitOfWorkj
    )
    {
        _dbContext = dbContext;
        _unitOfWork = unitOfWorkj;
    }

    public async Task<bool> createNewOtp(string otp, string email)
    {
        await deleteAllOtpBelongTo(email);
        ReseatePasswordOtp newOtp = new ReseatePasswordOtp();
        newOtp.IsValidated = false;
        newOtp.Otp = otp;
        newOtp.Email = email;
        newOtp.CreatedAt = DateTime.Now;
        newOtp.Id = Guid.NewGuid();
        await _unitOfWork.ReseatePasswordOtpRepository.addAsync(newOtp);
        int result = await _unitOfWork.Complate();
        if (result == 0) return false;
        return true;
    }

    public async Task<bool> updateOtpStatus(string otp)
    {
        await _dbContext
            .ReseatPasswords
            .Where(u => u.Otp == otp)
            .ExecuteUpdateAsync(otpHolder => otpHolder
                .SetProperty(value => value.IsValidated, true));

        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }

    private async Task<bool> deleteAllOtpBelongTo(string email)
    {
        await _dbContext.ReseatPasswords
            .Where(otp => otp.Email == email)
            .ExecuteDeleteAsync();
        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }


    public async Task<bool> isExist(string otp)
    {
     
            return await _dbContext.ReseatPasswords
                .AsNoTracking()
                .AnyAsync(u =>
                    (u.Otp == otp) && (u.CreatedAt.AddHours(1).Microsecond > DateTime.Now.Microsecond));
       
    }

    public async Task<bool> isExist(string email, string otp, bool status = false)
    {
    
            ReseatePasswordOtp? result = await _dbContext
                .ReseatPasswords
                .AsNoTracking()
                .FirstOrDefaultAsync(u =>
                    u.Otp == otp && u.IsValidated == status);

            if (result is null) return false;
            DateTime otpTime = result.CreatedAt.AddHours(1);

            return result.Email == email &&
                   (otpTime.Date == DateTime.Now.Date && otpTime.TimeOfDay > DateTime.Now.TimeOfDay);
        
    }
}