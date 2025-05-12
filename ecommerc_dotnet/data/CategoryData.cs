using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class CategoryData
{
    private readonly AppDbContext _dbContext;

    public CategoryData(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public List<CategoryResponseDto>? getCategories(int pageNumber=1, int pageSize=20)
    {
        
        try
        {
            var result = _dbContext.Category
                    
                .Where(c=>c.isBlocked==false)
                .OrderBy(c=>c.created_at)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(u=>new CategoryResponseDto
                {
                    id = u.id,
                    name = u.name,
                    image_path = u.image_path,
                });
               
            return result.ToList() ;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting user address" + ex.Message);

            return null;
        }
    }
 
 /*   public async Task<AddressResponseDto?> addUserLocation(
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
*/
    
}