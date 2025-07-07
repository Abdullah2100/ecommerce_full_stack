using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.midleware.ConfigImplment;
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

    public async Task<List<CategoryResponseDto>?>getCategories(
       
        IConfig services, 
        int pageNumber = 1,
        int pageSize = 20)
    {
        try
        {
            IQueryable<CategoryResponseDto> result = _dbContext.Categories
                .AsNoTracking()
                .Where(c => c.isBlocked == false)
                .OrderBy(c => c.createdAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(u => new CategoryResponseDto
                {
                    id = u.id,
                    name = u.name,
                    image = services.getKey("url_file") + u.image,
                });

            return await result.ToListAsync();
        }
        catch (Exception ex)
        {
            Console.Write("error from  getting category by pages " + ex.Message);

            return null;
        }
    }

    
    
    public async Task<Category?> getCategory(
        Guid id)
    {
        try
        {
            return await _dbContext.Categories
                .FindAsync(id);
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
            Category categoryObj = new Category
            {
                id = clsUtil.generateGuid(), 
                name = name, 
                image = filePath,
                isBlocked = false,
                ownerId = userId
            };
            await _dbContext.Categories.AddAsync(categoryObj);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  insert new category " + ex.Message);

            return false;
        }
    }


    public async Task<bool?> updateCategory(
        Guid id,
        string? name,
        string? filePath
    )
    {
        try
        {
            Category? category = await _dbContext.Categories.FindAsync(id);
            category!.name = name ?? category.name;
            category.image = filePath ?? category.image;

            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  update  category " + ex.Message);

            return false;
        }
    }


    public async Task<bool?> deleteCategory(
        Guid id
    )
    {
        try
        {
            Category? category = await _dbContext.Categories.FindAsync(id);
            if (category == null) return null;
             _dbContext.Remove(category);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  update  category " + ex.Message);

            return null;
        }
    }

    public async Task<bool> isExist
    (
        string name
    )
    {
        try
        {
            return await _dbContext.Categories.FirstOrDefaultAsync(c => c.name == name) != null;
        }
        catch (Exception ex)
        {
            Console.Write("error from  check if the category exist by name" + ex.Message);

            return false;
        }
    }
     public async Task<bool> isExist
        (
            Guid id 
        )
        {
            try
            {
                return await _dbContext.Categories.FirstOrDefaultAsync(c => c.id == id) != null;
            }
            catch (Exception ex)
            {
                Console.Write("error from  check if the category exist by name" + ex.Message);
    
                return false;
            }
        }
        
        
    
}