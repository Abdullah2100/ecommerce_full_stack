using api.application;
using api.domain.entity;
using api.domain.Interface;
using api.Presentation.dto;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ProductVariantRepository(AppDbContext context) : IProductVariantRepository
{
    public async Task<ProductVariant?> GetProductVarient(Guid productId, Guid id)
    {
        return await context.ProductVariants
            .FirstOrDefaultAsync(or => or.ProductId == productId && or.Id == id);
    }

    public void  AddProductVariants(ICollection<ProductVariant> productVariants)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
           context.ProductVariants.Add(productVariants.ElementAt(i));
        }

    }


    public void DeleteProductVariantByProductId(Guid productId)
    {
        var result = context.ProductVariants.Where(p => p.ProductId == productId).ToList();
        context.ProductVariants.RemoveRange(result);
    }

    public void DeleteProductVariant(List<CreateProductVariantDto> productVariants, Guid productId)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
            var result = context.ProductVariants.Where(pv =>
                pv.ProductId == productId && pv.VariantId == productVariants[i].VariantId &&
                pv.Name == productVariants[i].Name
            ).ToList();
            context.ProductVariants.RemoveRange(result);
        }

    }

    public void Add(ProductVariant entity)
    {
       context.ProductVariants.Add(entity);
    }

    public void Update(ProductVariant entity)
    {
        context.ProductVariants.Update(entity);
    }
}