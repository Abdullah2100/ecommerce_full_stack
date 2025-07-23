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

    private async Task<SubCategoryResponseDto?> getSubCategory(Guid id)
    {
        try
        {
            return await _dbContext.SubCategories
                .AsNoTracking()
                .Where(sub => sub.Id == id)
                .Select(sub =>
                     new SubCategoryResponseDto
                     {
                         id = sub.Id,
                         name = sub.Name,
                         categoryId = sub.CategoryId,
                         storeId = sub.StoreId

                     })
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }


    public async Task<List<SubCategoryResponseDto>> getSubCategory(
        Guid id,
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.SubCategories
                .AsNoTracking()
                .Where(sub => sub.StoreId == id)
                .Skip(((pageNumber - 1) * pageSize))
                .Take(pageSize)
                .Select(sub =>
                    new SubCategoryResponseDto
                    {
                        id = sub.Id,
                        name = sub.Name,
                        categoryId = sub.CategoryId,
                        storeId = sub.StoreId

                    })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return new List<SubCategoryResponseDto>();
        }
    }


    public async Task<bool?> isExist(Guid storeId, Guid subcategoryId)
    {
        try
        {
         return   await (from st in _dbContext.Stores
                                       join sub in _dbContext.SubCategories on st.Id equals sub.Id
                                       where st.Id == storeId
                                       select sub.Id == subcategoryId
                )
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }
    public async Task<bool?> isExist(Guid storeId, string name)
    {
        try
        {
            return await (from st in _dbContext.Stores
                          join sub in _dbContext.SubCategories on st.Id equals sub.Id
                          where st.Id == storeId
                          select sub.Name == name
                ).FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<int?> countByStoreId(Guid? storeId)
    {
        if (storeId is null) return null;
        try
        {
            return await _dbContext
                .SubCategories
                .Where(sub => sub.StoreId == storeId)
                .AsNoTracking()
                .CountAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from getting sub category by id " + ex.Message);
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
                           on sub.storeId equals st.id
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
        Guid storeId,
        string name)
    {
        try
        {
            Guid id = clsUtil.generateGuid();
            var result = await _dbContext
                .SubCategories.AddAsync(
                    new SubCategory
                    {
                        Id = id,
                        CategoryId = cateogy_id,
                        StoreId = storeId,
                        Name = name,
                        UpdatedAt = null,
                        CreatedAt = DateTime.Now,
                    }
                );
            await _dbContext.SaveChangesAsync();
            return await getSubCategory(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from adding new sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<SubCategoryResponseDto?> updateSubCategory(
        Guid id,
        string name,
        Guid categoryId)
    {
        try
        {
             await _dbContext
                .SubCategories.Where(sc=>sc.Id==id)
                .ExecuteUpdateAsync(sc=>
                    sc.SetProperty(
                    value=>value.UpdatedAt,DateTime.Now)
                    .SetProperty(value=>value.Name,name)
                    .SetProperty(value=>value.CategoryId,categoryId)
                );
            await _dbContext.SaveChangesAsync();
            return await getSubCategory(id);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from update sub category by id " + ex.Message);
            return null;
        }
    }

    public async Task<bool?> deleteSubCategory(Guid id)
    {
        try
        {
            await _dbContext
                .SubCategories.Where(sc=>sc.Id==id)
                .ExecuteDeleteAsync();
            
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error from update sub category by id " + ex.Message);
            return null;
        }
    }


}