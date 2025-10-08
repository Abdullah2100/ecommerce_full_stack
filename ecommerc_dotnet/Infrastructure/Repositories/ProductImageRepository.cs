using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ProductImageRepository(AppDbContext context) : IProductImageRepository
{
    public async Task<int> deleteProductImages(Guid id)
    {
        await context.ProductImages.Where(p => p.ProductId == id).ExecuteDeleteAsync();
        return await context.SaveChangesAsync();
    }

    public async Task<int> deleteProductImages(List<string> images, Guid id)
    {
        foreach (var image in images)
        {
            await context.ProductImages.Where(pi => pi.Path == image && pi.ProductId == id).ExecuteDeleteAsync();
        }

        return await context.SaveChangesAsync();
    }



    public async Task<bool> addProductImage(ICollection<ProductImage> productImage)
    {
        for (var i = 0; i < productImage.Count; i++)
        {
            await context.ProductImages.AddAsync(productImage.ElementAt(i));
        }

        return (await context.SaveChangesAsync()) > 0;
    }

    public async Task<List<string>> getProductImages(Guid id)
    {
        return await context.ProductImages
            .AsNoTracking()
            .Where(pi => pi.ProductId == id)
            .Select(pi => pi.Path)
            .ToListAsync();
    }

    public void add(ProductImage entity)
    {
        context.Add(entity);
    }

    public void update(ProductImage entity)
    {
        context.Update(entity);
    }
}