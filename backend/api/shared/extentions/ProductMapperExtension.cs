using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.extentions;

public static class ProductMapperExtension
{
   
    public static ProductDto ToDto(this Product product,string url)
    {
        return new ProductDto
        {
            Id = product.Id,
            Name = product.Name,
            Description = product.Description,
            Price = product.Price,
            Thmbnail = string.IsNullOrEmpty(product.Thmbnail) ? "" : url+ product.Thmbnail,
            CategoryId = product.SubCategory.CategoryId,
            ProductImages =product.ProductImages.Select(pi=>url+pi.Path).ToList(),
            ProductVarients = product.ProductVarients
                .GroupBy(pv => pv.VariantId, (key, g)
                    => g.Select(pvH=>pvH.ToProductVarientDto()).ToList()
                ).ToList(),
            StoreId = product.StoreId,
            SubcategoryId = product.SubcategoryId,
        };
    }
 
    public static AdminProductsDto ToAdminDto(this Product product,string url)
    {
        return new AdminProductsDto 
        {
            Id = product.Id,
            Name = product.Name,
            Description = product.Description,
            Price = product.Price,
            Thmbnail = string.IsNullOrEmpty(product.Thmbnail) ? "" : url+ product.Thmbnail,
            StoreName = product.Store.Name,
            ProductImages =product.ProductImages.Select(pi=>url+pi.Path).ToList(),
            ProductVarients = product.ProductVarients
                .GroupBy(pv => pv.VariantId, (key, g)
                    => g.Select(pvH=>pvH.ToAdminProductVarientDto()).ToList()
                ).ToList(),
            Subcategory =  product.SubCategory.Name,
        };
    }

    public static bool IsEmpty(this UpdateProductDto dto)
    {
        return string.IsNullOrEmpty(dto.Name) &&
               string.IsNullOrEmpty(dto.Description)
               && dto.Thmbnail == null
               && dto.SubcategoryId == null
               && dto.Price == null
               && dto.ProductVarients == null
               && dto.Images == null
               && dto.Deletedimages == null
               && dto.DeletedProductVarients==null
            ;    
    }


    
}