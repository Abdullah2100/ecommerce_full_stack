using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.module;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class SubCategoryData 
{
    private readonly AppDbContext _dbContext;

    public SubCategoryData(AppDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<SubCategoryResponseDto?> getSubCategory(Guid id)
    {
        try
        {
            return await _dbContext.SubCategory
                .AsNoTracking()
                .Where(sub=>sub.id==id)
                   
                .Select(sub=>
                     new SubCategoryResponseDto
                     {
                         id = sub.id,
                         name = sub.name,
                         category_id = sub.categori_id,
                    store_id=sub.categori_id
                    
                })
                .FirstOrDefaultAsync(); 
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id "+ex.Message);
            return null;
        }
    }
    
    
    public async Task<List<SubCategoryResponseDto>> getSubCategory(
        Guid id,
        int pageNumber,
        int pageSize=25)
    {
        try
        {
            return await _dbContext.SubCategory
                .AsNoTracking()
                .Where(sub=>sub.store_id==id)
                .Skip(((pageNumber-1)*pageSize))
                .Take(pageSize)
                .Select(sub=>
                    new SubCategoryResponseDto
                    {
                        id = sub.id,
                        name = sub.name,
                        category_id = sub.categori_id,
                        store_id=sub.store_id
                    
                    })
                .ToListAsync(); 
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id "+ex.Message);
            return new List<SubCategoryResponseDto>();
        }
    }

    
    public async Task<bool?> isExist(Guid store_id,Guid subCategory_id)
       {
           try
           {
               IQueryable<bool> result = (from st in _dbContext.Store
                       join sub in _dbContext.SubCategory on st.id equals sub.id
                       where st.id==store_id
                       select sub.id == subCategory_id 
                   );
               return await result.FirstOrDefaultAsync();
           }
           catch (Exception ex)
           {
               Console.WriteLine("this error from getting sub category by id "+ex.Message);
               return null;
           }
       }
     public async Task<bool?> isExist(Guid store_id,string name )
           {
               try
               {
                   var result = (from st in _dbContext.Store
                           join sub in _dbContext.SubCategory on st.id equals sub.id
                           where st.id==store_id
                           select sub.name == name 
                       );
                   return await result.FirstOrDefaultAsync();
               }
               catch (Exception ex)
               {
                   Console.WriteLine("this error from getting sub category by id "+ex.Message);
                   return null;
               }
           }
  
           public async Task<int?> countByStoreId(Guid? store_id)
           {
               if (store_id == null) return null;
               try
               {
                   return  await _dbContext.SubCategory.Where(sub => sub.store_id == store_id).CountAsync();
               }
               catch (Exception ex)
               {
                   Console.WriteLine("this error from getting sub category by id "+ex.Message);
                   return null;
               }
           }


     
     
 /*   public static List<SubCategoryResponseDto>? getSubCategories(Guid id,AppDbContext dbContext,IConfigurationServices config)
    {
        try
        {
            return  (
                    from sub in dbContext.subCategory
                    join st in dbContext.Store
                        on sub.store_id equals st.id
                    select new SubCategoryResponseDto
                    {
                        id = sub.id,
                        name = sub.name,
                    }
                )
                .AsNoTracking()
                .ToList();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id "+ex.Message);
            return null;
        }
    }
*/
    public async Task<SubCategoryResponseDto?> createSubCategory(
        Guid cateogy_id,
        Guid store_id, 
        string name)
    {
        try
        {
            Guid id = clsUtil.generateGuid();
            var result = await _dbContext
                .SubCategory.AddAsync(
                    new SubCategory
                    {
                        id = id,
                       categori_id = cateogy_id,
                        store_id = store_id,
                        name = name,
                        updated_at = null,
                        created_at = DateTime.Now,
                    }
                );
            await _dbContext.SaveChangesAsync();
            return await getSubCategory(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from adding new sub category by id "+ex.Message);
            return null;
        }
    }
    
     public async Task<SubCategoryResponseDto?> updateSubCategory(Guid id,
         string name, Guid category_id)
       {
           try
           {
               SubCategory? result = await  _dbContext
                   .SubCategory.FindAsync(id);
               if (result == null) return null;
               
               result.updated_at = DateTime.Now;
               result.name = name;
               result.categori_id = category_id;
               await _dbContext.SaveChangesAsync();
               return await getSubCategory(id);
           }
           catch (Exception ex)
           {
               Console.WriteLine("this error from update sub category by id "+ex.Message);
               return null;
           }
       }
      
     public async Task<bool?> deleteSubCategory(Guid id)
           {
               try
               {
                   SubCategory? result = await  _dbContext
                       .SubCategory.FindAsync(id);
                   if (result == null) return null;
                   _dbContext.Remove(result);
                   await _dbContext.SaveChangesAsync();
                   return true;
               }
               catch (Exception ex)
               {
                   Console.WriteLine("this error from update sub category by id "+ex.Message);
                   return null;
               }
           }
        
    
}