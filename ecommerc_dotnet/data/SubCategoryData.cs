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
                })
               
                .FirstOrDefaultAsync(); 
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
}