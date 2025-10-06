using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.data;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.mapper;

public static class ProductMapperExtention
{
   
    public static ProductDto toDto(this Product product,string url)
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
                .GroupBy(pv => pv.VarientId, (key, g)
                    => g.Select(pvH=>pvH.toProductVarientDto()).ToList()
                ).ToList(),
            StoreId = product.StoreId,
            SubcategoryId = product.SubcategoryId,
        };
    }
 
    public static AdminProductsDto toAdminDto(this Product product,string url)
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
                .GroupBy(pv => pv.VarientId, (key, g)
                    => g.Select(pvH=>pvH.toAdminProductVarientDto()).ToList()
                ).ToList(),
            Subcategory =  product.SubCategory.Name,
        };
    }

    public static bool isEmpty(this UpdateProductDto dto)
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