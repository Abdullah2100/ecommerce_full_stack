using System.Threading.Tasks;
using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class ForgetPasswordData
{
    private readonly AppDbContext _dbContext;
    // private readonly ILogger _logger;

    public ForgetPasswordData(AppDbContext dbContext
    // , ILogger logger
    )
    {
        _dbContext = dbContext;
        // _logger = logger;
    }

    public async Task<bool> createNewOtp(string otp, string email)
    {
        try
        {
            await deleteAllOtpBelongTo(email);
            ReseatePasswordOtp newOtp = new ReseatePasswordOtp();
            newOtp.isValidated = false;
            newOtp.otp = otp;
            newOtp.email = email;
            newOtp.createdAt = DateTime.Now;
            newOtp.id = Guid.NewGuid();
            _dbContext.ReseatPasswords.Add(newOtp);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User" + e.Message);
            return false;
        }
         
                
    }

    public async Task<bool> updateOtpStatus(string otp)
    {
        try
        {
            await _dbContext
                .ReseatPasswords
                .Where(u => u.otp == otp)
                .ExecuteUpdateAsync(otp=>otp
                    .SetProperty(value=>value.isValidated,true));
            
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from create new User"+e.Message);
            Console.Write("error from create new User" + e.Message);
            return false;
        }
    }

    private async Task<bool> deleteAllOtpBelongTo(string email)
    {
        try
        {
          await _dbContext.ReseatPasswords
              .Where(otp => otp.email == email)
               .ExecuteDeleteAsync();
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from delete all otp belong to email" + e.Message);
            return false;
        }
    }


    public bool isExist(string otp)
    {
        try
        {
            return _dbContext.ReseatPasswords
                .AsNoTracking()
                .FirstOrDefault(u =>
                (u.otp == otp) && (u.createdAt.AddHours(1).Microsecond > DateTime.Now.Microsecond)) !=  null;
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }

    public async Task<bool> isExist(string email, string otp,bool status=false)
    {
        try
        {
            ReseatePasswordOtp? result = await _dbContext
                .ReseatPasswords
                .AsNoTracking()
                .FirstOrDefaultAsync(u =>
            u.otp == otp&&u.isValidated==status);

            if (result is null) return false;
            DateTime otpTime =result.createdAt.AddHours(1);

            return  result.email == email && (otpTime.Date == DateTime.Now.Date && otpTime.TimeOfDay > DateTime.Now.TimeOfDay);
        }
        catch (Exception e)
        {
            //_logger.LogError("error from get user by username"+e.Message);
            Console.WriteLine("error from get user by username" + e.Message);
            return false;
        }
    }
}