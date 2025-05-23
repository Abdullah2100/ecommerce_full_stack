using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.module;
using hotel_api.util;

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

    public async Task<bool> createNewOtp(string otp,string email)
    {
        try
        {
            deleteAllOtpBelongTo(email);
            var newOtp = new ReseatePasswordOtp();
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

    public async Task<bool> deleteAllOtpBelongTo(string email)
    {
        try
        {
            var otpBelongList = _dbContext.ReseatPassword.Where(otp => otp.email == email);
            _dbContext.ReseatPassword.RemoveRange(otpBelongList);
          await  _dbContext.SaveChangesAsync();
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

  
    
}