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
            var newOtp = new ReseatePasswordOtp();
            newOtp.isValidated = false;
            newOtp.otp = otp;
            newOtp.email = email;
            newOtp.createdAt = DateTime.Now;
            newOtp.id = Guid.NewGuid();
            _dbContext.ReseatPassword.Add(newOtp);
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
            var result = _dbContext.ReseatPassword.FirstOrDefault(u => u.otp == otp);
            if (result == null)
            {
                return false;
            }
            result.isValidated = true;
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
            var otpBelongList = _dbContext.ReseatPassword.Where(otp => otp.email == email);
            _dbContext.ReseatPassword.RemoveRange(otpBelongList);
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
            return _dbContext.ReseatPassword.FirstOrDefault(u =>
                (u.otp == otp) && (u.createdAt.AddHours(1).Microsecond > DateTime.Now.Microsecond)) != null;
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
            var result = await _dbContext.ReseatPassword.Where(u =>
            u.otp == otp&&u.isValidated==status).FirstOrDefaultAsync();

            if (result == null) return false;
            var otpTime =result.createdAt.AddHours(1);

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