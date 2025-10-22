using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.domain.entity;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using NuGet.DependencyResolver;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ProductRepository(
    AppDbContext context
  ) : IProductRepository
{
    public async Task<IEnumerable<Product>> getAllAsync(int page, int length)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    

    public void add(Product entity)
    {
       // try
        //{
           // var result = true;
           context.Products.Add(new Product
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                SubcategoryId = entity.SubcategoryId,
                StoreId = entity.StoreId,
                Price = entity.Price,
                CreatedAt = DateTime.Now,
                UpdatedAt = null,
                Thmbnail = entity.Thmbnail
            });
          /*  result =   await context.SaveChangesAsync() >0;
            if (entity.ProductVarients is not null)
            {
                result = await productVariantRepository.addProductVariants(entity.ProductVarients);
                if (result != false)
                {
                    Console.WriteLine("could not save product image: ");
                    return 0;
                }
            }

            if (entity.ProductImages is not null)
            {
                result = await productImageRepository.addProductImage(entity.ProductImages);
                if (!result)
                {
                    Console.WriteLine("could not save product image: ");
                    return 0;
                }
            }

       /*     return result==true? 1: 0;
        }
        catch (Exception ex)
        {
            Console.WriteLine("Error: fom create new Peoduct " + ex.Message);
            return 0;
        }*/
    }

    public void  update(Product entity)
    {
        var result = true;
        
        var product = context.Products.Find(entity.Id);
        if (product == null) throw new ArgumentNullException();
        
       /* if (entity.ProductVarients is not null)
        {
            result =  productVariantRepository.addProductVariants(entity.ProductVarients);
            if (result != false)
            {
                Console.WriteLine("could not save product image: ");
                return 0;
            }
        } 

       /* if (entity.ProductImages is not null)
            if (entity.ProductImages is not null)
            { 
                result = await productImageRepository.addProductImage(entity.ProductImages);
                if (result != false) return await context.SaveChangesAsync();
                Console.WriteLine("could not save product image: ");
                return 0;
            }
        */

        context.Products.Update(entity);


    }

    public void delete(Guid id)
    
    {
       // await productImageRepository.deleteProductImages(id);
        //await context.ProductVarients.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        //await productVariantRepository.deleteProductVariantByProductId(id);
        var product= context.Products.Find(id);
        if (product == null) throw new ArgumentNullException();
        context.Products.Remove(product);
    }

    public async Task<Product?> getProduct(Guid id)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id);
    }

    public async Task<Product?> getProduct(Guid id, Guid storeId)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.StoreId == storeId);
    }

    public async Task<Product?> getProductByUser(Guid id, Guid userId)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.Store.UserId == userId);
    }

    public async Task<IEnumerable<Product>> getProducts(
        Guid storeId,
        Guid subCategoryId,
        int pageNum,
        int pageSize
    )
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(p => p.StoreId == storeId && p.SubcategoryId == subCategoryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> getProducts(
        Guid storeId,
        int pageNum,
        int pageSize)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(p => p.StoreId == storeId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> getProducts(int page, int length)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> getProductsByCategory(
        Guid categoryId,
        int pageNum,
        int pageSize
    )
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(p => p.SubCategory.CategoryId == categoryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }


    public async Task<bool> isExist(Guid id)
    {
        return await context.Products.FindAsync(id) != null;
    }
    

  

   
}