using api.domain.entity;
using api.domain.Interface;
using api.Presentation.dto;
using ecommerc_dotnet.application;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ProductVariantRepository(AppDbContext context) : IProductVariantRepository
{
    public async Task<ProductVariant?> GetProductVarient(Guid productId, Guid id)
    {
        return await context.ProductVarients
            .FirstOrDefaultAsync(or => or.ProductId == productId && or.Id == id);
    }

    public void  AddProductVariants(ICollection<ProductVariant> productVariants)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
           context.ProductVarients.Add(productVariants.ElementAt(i));
        }

    }


    public void DeleteProductVariantByProductId(Guid productId)
    {
        var result = context.ProductVarients.Where(p => p.ProductId == productId).ToList();
        context.ProductVarients.RemoveRange(result);
    }

    public void DeleteProductVariant(List<CreateProductVariantDto> productVariants, Guid productId)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
            var result = context.ProductVarients.Where(pv =>
                pv.ProductId == productId && pv.VariantId == productVariants[i].VariantId &&
                pv.Name == productVariants[i].Name
            ).ToList();
            context.ProductVarients.RemoveRange(result);
        }

    }

    public void Add(ProductVariant entity)
    {
       context.ProductVarients.Add(entity); 
    }

    public void Update(ProductVariant entity)
    {
        context.ProductVarients.Update(entity);
    }
}