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
                .Where(c => c.IsBlocked == false)
                .AsNoTracking()
                .OrderBy(c => c.CreatedAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(u => new CategoryResponseDto
                {
                    id = u.Id,
                    name = u.Name,
                    image = services.getKey("url_file") + u.Image,
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
                Id = clsUtil.generateGuid(), 
                Name = name, 
                Image = filePath,
                IsBlocked = false,
                OwnerId = userId
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
            category!.Name = name ?? category.Name;
            category.Image = filePath ?? category.Image;

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
            await _dbContext.Categories
                .Where(ca => ca.Id == id)
                .ExecuteDeleteAsync();
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
            return await _dbContext
                .Categories
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.Name == name) !=  null;
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
                return await _dbContext
                    .Categories
                    .AsNoTracking()
                    .FirstOrDefaultAsync(c => c.Id == id) !=  null;
            }
            catch (Exception ex)
            {
                Console.Write("error from  check if the category exist by name" + ex.Message);
    
                return false;
            }
        }
        
        
    
}