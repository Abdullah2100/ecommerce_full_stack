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
        int pageNumber = 1,
        int pageSize = 20)
    {
        try
        {
            var result = _dbContext.Category
                .AsNoTracking()
                .Where(c => c.isBlocked == false)
                .OrderBy(c => c.created_at)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(u => new CategoryResponseDto
                {
                    id = u.id,
                    name = u.name,
                    image_path = services.getKey("url_file") + u.image_path,
                });

            return result.ToList();
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
            return await _dbContext.Category.FindAsync(id);
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
            var categoryObj = new Category
            {
                id = clsUtil.generateGuid(), name = name, image_path = filePath, isBlocked = false, owner_id = userId
            };
            await _dbContext.Category.AddAsync(categoryObj);
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
            var category = await _dbContext.Category.FindAsync(id);
            category!.name = name ?? category.name;
            category.image_path = filePath ?? category.image_path;

            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  update  category " + ex.Message);

            return false;
        }
    }


    public async Task<bool?> blockOrUnBlockCategory(
        Guid id
    )
    {
        try
        {
            var category = await _dbContext.Category.FindAsync(id);
            category!.isBlocked = !category.isBlocked;
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.Write("error from  update  category " + ex.Message);

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
            return await _dbContext.Category.FirstOrDefaultAsync(c => c.name == name) != null;
        }
        catch (Exception ex)
        {
            Console.Write("error from  check if the category exist by name" + ex.Message);

            return false;
        }
    }
}