using ecommerc_dotnet.context;
using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;
using NuGet.DependencyResolver;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ProductRepository : IProductRepository
{
    private readonly AppDbContext _context;

    public ProductRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Product>> getAllAsync(int page, int length)
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<int> addAsync(Product entity)
    {
        await _context.Products.AddAsync(new Product
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

        if (entity.ProductVarients is not null)
            foreach (var x in entity.ProductVarients)
            {
                await _context.ProductVarients.AddAsync(new ProductVarient
                {
                    Id = clsUtil.generateGuid(),
                    Precentage = x.Precentage == 0 ? 1 : (decimal)x.Precentage!,
                    VarientId = x.VarientId,
                    ProductId = entity.Id,
                    Name = x.Name
                });
            }

        if (entity.ProductImages is not null)
            foreach (var productImage in entity.ProductImages)
            {
                await _context.ProductImages.AddAsync(new ProductImage
                {
                    Id = productImage.Id,
                    Path = productImage.Path,
                    ProductId = entity.Id,
                });
            }

        return await _context.SaveChangesAsync();
    }

    public async Task<int> updateAsync(Product entity)
    {
        if (entity.ProductVarients is not null)
            foreach (var x in entity.ProductVarients)
            {
                await _context.ProductVarients.AddAsync(new ProductVarient
                {
                    Id = clsUtil.generateGuid(),
                    Precentage = x.Precentage == 0 ? 1 : (decimal)x.Precentage!,
                    VarientId = x.VarientId,
                    ProductId = entity.Id,
                    Name = x.Name
                });
            }

        if (entity.ProductImages is not null)
            foreach (var productImage in entity.ProductImages)
            {
                await _context.ProductImages.AddAsync(new ProductImage
                {
                    Id = productImage.Id,
                    Path = productImage.Path,
                    ProductId = entity.Id,
                });
            }
        
        _context.Products.Update(new Product
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

      

        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteAsync(Guid id)
    {
        await _context.ProductImages.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        await _context.ProductVarients.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        await _context.Products.Where(p => p.Id == id).ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public async Task<Product?> getProduct(Guid id)
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id);
    }

    public async Task<Product?> getProduct(Guid id, Guid storeId)
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.StoreId==storeId); 
    }

    public async Task<Product?> getProductByUser(Guid id, Guid userId)
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.store.UserId==userId); 

    }

    public async Task<IEnumerable<Product>> getProducts(
        Guid storeId,
        Guid subCategoryId,
        int pageNum,
        int pageSize
    )
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
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
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(p => p.StoreId == storeId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> getProductsByCategory(
        Guid categoryId,
        int pageNum,
        int pageSize
    )
    {
        return await _context.Products
            .AsNoTracking()
            .Include(pro=>pro.store)
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(p => p.subCategory.CategoryId == categoryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }


    public async Task<bool> isExist(Guid id)
    {
        return await _context.Products.FindAsync(id) != null;
    }

    public async Task<int> deleteProductVarient(Guid id)
    {
        await _context.ProductVarients.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteProductImages(Guid id)
    {
        await _context.ProductImages.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteProductImages(List<string> images, Guid id)
    {
        foreach (var image in images)
        {
          await _context.ProductImages.Where(pi=>pi.Path==image&&pi.ProductId==id).ExecuteDeleteAsync(); 
        }
        return await _context.SaveChangesAsync();
    }

    public async Task<int> deleteProductVarient(List<CreateProductVarientDto> productVarients, Guid productId)
    {
        productVarients.ForEach((x) =>
        {
            _context.ProductVarients
                .Include(pi => pi.VarientId)
                .Where(pv =>
                    pv.ProductId == productId && pv.VarientId == x.VarientId && pv.Name == x.Name)
                .ExecuteDelete();
        });
        return await _context.SaveChangesAsync();
    }

    public async Task<List<string>> getProductImages(Guid id)
    {
        return await _context.ProductImages
            .AsNoTracking()
            .Where(pi => pi.ProductId == id)
            .Select(pi => pi.Path)
            .ToListAsync();
    }
}