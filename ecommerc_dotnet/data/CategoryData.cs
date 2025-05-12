using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
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

    public List<CategoryResponseDto>? getCategories(
        IConfigurationServices services,
        int pageNumber=1, 
        int pageSize=20)
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
                    image_path =services.getKey("url_file") + u.image_path,
                });
               
            return result.ToList() ;

        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }
 
   public async Task<bool?> addNewCategory(
        string name,
        string filePath,
        Guid userId)
    {
        try
        {
            var categoryObj = new Category{id = clsUtil.generateGuid(),name=name, image_path=filePath, isBlocked=false,owner_id=userId};
            await _dbContext.Category.AddAsync(categoryObj);
            await _dbContext.SaveChangesAsync();
            return  true;
            
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new category " + ex.Message);

            return false;
        }
    }

    public async Task<bool> isExistByName
    (
        string name
            )
    {
        try
        {
         return    await _dbContext.Category.FirstOrDefaultAsync(c=>c.name == name)!=null;
            
        }
        catch (Exception ex)
        {
            Console.Write("error from  check if the category exist by name" + ex.Message);

            return false;
        }
    }
 
  /*  public async Task<bool?> changeCurrentAddress(
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