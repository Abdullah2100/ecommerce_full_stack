using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ProductImageRepository(AppDbContext context) : IProductImageRepository
{
    public void  DeleteProductImages(Guid id)
    {
        var result = context.ProductImages.FirstOrDefault(p => p.ProductId == id);
        if (result != null) throw new ArgumentNullException();
        context.ProductImages.Remove(result);
    }

    public void DeleteProductImages(List<string> images, Guid id)
    {
        foreach (var image in images)
        {
         var result =    context.ProductImages.Where(pi => pi.Path == image && pi.ProductId == id).ToList();
         context.ProductImages.RemoveRange(result);
        }

    }



    public  void AddProductImage(ICollection<ProductImage> productImage)
    {
        for (var i = 0; i < productImage.Count; i++)
        {
             context.ProductImages.Add(productImage.ElementAt(i));
        }
    }

    public async Task<List<string>> GetProductImages(Guid id)
    {
        return await context.ProductImages
            .AsNoTracking()
            .Where(pi => pi.ProductId == id)
            .Select(pi => pi.Path)
            .ToListAsync();
    }

    public void Add(ProductImage entity)
    {
        context.Add(entity);
    }

    public void Update(ProductImage entity)
    {
        context.Update(entity);
    }
}