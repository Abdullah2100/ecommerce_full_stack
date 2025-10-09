using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ProductImageRepository(AppDbContext context) : IProductImageRepository
{
    public void  deleteProductImages(Guid id)
    {
        var result = context.ProductImages.FirstOrDefault(p => p.ProductId == id);
        if (result != null) throw new ArgumentNullException();
        context.ProductImages.Remove(result);
    }

    public void deleteProductImages(List<string> images, Guid id)
    {
        foreach (var image in images)
        {
         var result =    context.ProductImages.Where(pi => pi.Path == image && pi.ProductId == id).ToList();
         context.ProductImages.RemoveRange(result);
        }

    }



    public  void addProductImage(ICollection<ProductImage> productImage)
    {
        for (var i = 0; i < productImage.Count; i++)
        {
             context.ProductImages.Add(productImage.ElementAt(i));
        }
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