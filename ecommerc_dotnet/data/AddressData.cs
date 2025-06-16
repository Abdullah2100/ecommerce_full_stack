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
                .AsNoTracking()
                .Where(ad => ad.id == addressId)
                .Select(ad => new AddressResponseDto
                {
                    id = ad!.id,
                    longitude = ad.longitude,
                    latitude = ad.latitude,
                    title = ad.title,
                    isCurrent = ad.isCurrent,
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
                .AsNoTracking()
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
            await _dbContext.Address.ExecuteUpdateAsync(ad=>ad.SetProperty(state=>state.isCurrent,false));

            Address addressObj = new Address
            {
                longitude = longitude,
                latitude = latitude,
                title = title,
                isCurrent = true,
                id = clsUtil.generateGuid(),
                created_at = DateTime.Now,
                owner_id = userId
            };
            
            await _dbContext.Address.AddAsync(addressObj);
            await _dbContext.SaveChangesAsync();
            return  await getUserAddressByAddressId(addressObj.id);

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
                .Where(ad => ad.owner_id == userId && ad.id != addressId)
                .ExecuteUpdateAsync(ad => ad.SetProperty(va => va.isCurrent, true));
           
            Address? currentAddress =await  _dbContext.Address.FindAsync(addressId);
           
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
    
    public async Task<AddressResponseDto?> updateAddress(
        Guid addressId,
        string? titile=null,
        decimal? longitude=null,
        decimal? latitude=null
    )
    {
        
        try
        {

            
            Address? currentAddress =await  _dbContext.Address.FirstOrDefaultAsync(ad=>ad.id==addressId);

            if (currentAddress == null) return null;
            currentAddress.title = titile??currentAddress.title;
            currentAddress.longitude = longitude??currentAddress.longitude;
            currentAddress.latitude = latitude ?? currentAddress.latitude;
            
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
               .CountAsync(u => u.owner_id == userId && u.id != userId);
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
    //             .Where(ad => ad.owner_id == id)
    //             .OrderByDescending(ad=>ad.created_at )
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