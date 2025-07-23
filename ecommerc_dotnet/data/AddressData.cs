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

    public async Task<AddressResponseDto?> getUserAddressByAddressId(Guid addressId)
    {
        
        try
        {
            IQueryable<AddressResponseDto> result =  _dbContext.Address
                .Where(ad => ad.Id == addressId)
                .AsNoTracking()
                .Select(ad => new AddressResponseDto
                {
                    id = ad!.Id,
                    longitude = ad.Longitude,
                    latitude = ad.Latitude,
                    title = ad.Title,
                    isCurrent = ad.IsCurrent,
                });
            return await result.FirstOrDefaultAsync();

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }

    public async Task<Address?> getAddressData(Guid addressId)
    {
        
        try
        {
            Address? result =await _dbContext.Address.FindAsync(addressId);
            return  result;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }


    
    public async Task<List<AddressResponseDto>?> getUserAddressByUserId(Guid userId)
    {
        
        try
        {
             return await   _dbContext.Address
                .Where(ad => ad.OwnerId == userId)
                .AsNoTracking()
                .OrderByDescending(ad=>ad.CreatedAt )
                .Select(ad =>
                    new AddressResponseDto
                    {
                        id = ad.Id,
                        longitude = ad.Longitude,
                        latitude = ad.Latitude,
                        title = ad.Title, isCurrent = ad.IsCurrent,
                    })
                .ToListAsync();

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }
    
    public async Task<AddressResponseDto?> addUserLocation(
     decimal longitude, 
     decimal latitude,
     string title , 
        Guid userId)
    {
        try
        {
            await _dbContext.Address.ExecuteUpdateAsync(ad=>ad.SetProperty(state=>state.IsCurrent,false));

            Address addressObj = new Address
            {
                Longitude = longitude,
                Latitude = latitude,
                Title = title,
                IsCurrent = true,
                Id = clsUtil.generateGuid(),
                CreatedAt = DateTime.Now,
                OwnerId = userId
            };
            
            await _dbContext.Address.AddAsync(addressObj);
            await _dbContext.SaveChangesAsync();
            return  await getUserAddressByAddressId(addressObj.Id);

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

            await _dbContext.Address
                .Where(ad => ad.OwnerId == userId && ad.Id !=  addressId)
                .ExecuteUpdateAsync(ad => ad.SetProperty(va => va.IsCurrent, false));
           
            Address? currentAddress =await  _dbContext.Address.FindAsync(addressId);
           
           if(currentAddress!= null)
               currentAddress.IsCurrent = true;
            
            await _dbContext.SaveChangesAsync();
            
            return  true;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }
    
    public async Task<AddressResponseDto?> updateAddress(
        Guid addressId,
        string? titile=null,
        decimal? longitude=null,
        decimal? latitude=null
    )
    {
        
        try
        {

            
            Address? currentAddress =await  _dbContext.Address.FirstOrDefaultAsync(ad=>ad.Id==addressId);

            if (currentAddress is null) return null;
            currentAddress.Title = titile??currentAddress.Title;
            currentAddress.Longitude = longitude??currentAddress.Longitude;
            currentAddress.Latitude = latitude ?? currentAddress.Latitude;
            
            await _dbContext.SaveChangesAsync();
            
            return await getUserAddressByAddressId(addressId);

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }
    
    
    public async Task<bool> deleteDaddress(
        Guid addressId 
    )
    {
        
        try
        {
            Address? addressData = await _dbContext.Address.FindAsync(addressId);
            if (addressData==null) return false;
            await _dbContext.SaveChangesAsync();
            return true;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return false;
        }
    }

    public async Task<int>addressCountForUser(
        Guid userId
    )
    {
        
        try
        {

           return await  _dbContext.Address
               .AsNoTracking()
               .CountAsync(u => u.OwnerId == userId && u.Id !=  userId);
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return 0;
        }
    }

    // public static List<AddressResponseDto>? getUserAddressByUserId(Guid id,AppDbContext _dbContext)
    // {
    //     
    //     try
    //     {
    //         var result =  _dbContext.Address
    //             .AsNoTracking()
    //             .Where(ad => ad.ownerId == id)
    //             .OrderByDescending(ad=>ad.createdAt )
    //             .Select(ad =>
    //                 new AddressResponseDto
    //                 {
    //                     id = ad.id,
    //                     longitude = ad.longitude,
    //                     latitude = ad.latitude,
    //                     title = ad.title, isCurrent = ad.isCurrent,
    //                 })
    //             .ToList();
    //         return result;
    //
    //     }
    //     catch (Exception ex)
    //     {
    //         Console.Write("error from  getting user address" + ex.Message);
    //
    //         return null;
    //     }
    // }

    
}