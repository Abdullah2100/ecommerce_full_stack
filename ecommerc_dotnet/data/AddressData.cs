using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class AddressData
{
    private readonly AppDbContext _dbContext;

    public AddressData(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public AddressResponseDto? getUserLocation(Guid addressId)
    {
        
        try
        {
            var result = _dbContext.Address
                .FirstOrDefault(ad => ad.id == addressId);
                
                var addressHolder = new AddressResponseDto
                {
                    id = result.id,
                    longitude = result.longitude,
                    latitude = result.latitude,
                    title = result.title, 
                    isCurrent = result.isCurrent,
                };
            return addressHolder;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }

    public async Task<List<AddressResponseDto>?> getUserLocations(Guid userId)
    {
        
        try
        {
            var result =await   _dbContext.Address
                .Where(ad => ad.owner_id == userId)
                .OrderByDescending(ad=>ad.created_at )
                .Select(ad =>
                    new AddressResponseDto
                    {
                        id = ad.id,
                        longitude = ad.longitude,
                        latitude = ad.latitude,
                        title = ad.title, isCurrent = ad.isCurrent,
                    })
                .ToListAsync();
            return result;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }
    
    public async Task<AddressResponseDto?> addUserLocation(
        AddressRequestDto address,
        Guid userId)
    {
        
        try
        {

            var addressObj = new Address();
            addressObj.id = clsUtil.generateGuid();
            addressObj.longitude = address.longitude;
            addressObj.latitude = address.latitude;
            addressObj.title = address.title;
            addressObj.owner_id = userId;
            _dbContext.Address.Add(addressObj);
            await _dbContext.SaveChangesAsync();
            
            return  getUserLocation(addressObj.id);

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }

    
    public async Task<bool?> changeCurrentAddress(
        Guid addressId,
        Guid userId
        )
    {
        
        try
        {

            var result = _dbContext.Address.Where(ad => ad.owner_id == userId && ad.id != addressId);
            await result
                .ForEachAsync(u => u.isCurrent = false);
           var currentAddress =  _dbContext.Address.FirstOrDefault(ad=>ad.id==addressId);
           
           if(currentAddress!=null)
               currentAddress.isCurrent = true;
            
            await _dbContext.SaveChangesAsync();
            
            return  true;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }

    
}