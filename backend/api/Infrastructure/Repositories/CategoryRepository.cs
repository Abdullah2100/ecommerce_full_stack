using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class CategoryRepository(AppDbContext context) : ICategoryRepository
{
 

    public void  add(Category entity)
    {
         context.Categories.Add(entity);
    }

    public void  update(Category entity)
    {
        context.Categories.Update(entity);
    }
    

    public async Task<Category?> getCategory(Guid id)
    {
        return await context.Categories.FindAsync(id);
    }

    public async Task<List<Category>> getCategories(int page, int length)
    {
        return await context
            .Categories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync(); 
    }

    public async Task<bool> isExist(Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Id == id);
    }

    public async Task<bool> isExist(string name)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name);
    }

    public async Task<bool> isExist(string name,Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name && e.Id != id);
    }
    
    public  void delete(Guid id)
    {
        var category= context.Categories.FirstOrDefault(ca => ca.Id == id);
        if (category is null) throw new ArgumentNullException();
        context.Categories.Remove(category);
    }
    
}