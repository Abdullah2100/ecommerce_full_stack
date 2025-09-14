using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.infrastructure.repositories;

public class ProductVariantRepository(AppDbContext context) : IProductVariantRepository
{
    public async Task<ProductVarient?> getProductVarient(Guid productId, Guid id)
    {
        return await context.ProductVarients
            .FirstOrDefaultAsync(or => or.ProductId == productId && or.Id == id);
    }

    public async Task<bool> addProductVariants(ICollection<ProductVarient> productVariants)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
            await context.ProductVarients.AddAsync(productVariants.ElementAt(i));
        }

        return (await context.SaveChangesAsync()) > 0;
    }


    public async Task<int> deleteProductVariantByProductId(Guid productId)
    {
        await context.ProductVarients.Where(p => p.ProductId == productId).ExecuteDeleteAsync();
        return 1;
    }

    public async Task<int> deleteProductVariant(List<CreateProductVarientDto> productVariants, Guid productId)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
            await context.ProductVarients.Where(pv =>
                pv.ProductId == productId && pv.VarientId == productVariants[i].VarientId &&
                pv.Name == productVariants[i].Name
            ).ExecuteDeleteAsync();
        }

        ;
        return 1;
    }
}