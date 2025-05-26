using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class ProductData
{
    public ProductData(
        AppDbContext dbContext,
        IConfig config,
        IWebHostEnvironment host)
    {
        _dbContext = dbContext;
        _config = config;
        _host = host;
    }

    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IWebHostEnvironment _host;

    public ProductResponseDto? getProduct(Guid id)
    {
        try
        {
            return  _dbContext.Products
                .AsNoTracking()
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.id == id)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategory_id = pr.subcategory_id,
                    store_id = pr.store_id,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.product_id == pr.id)
                        .GroupBy(pv => pv.varient_id, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varient_id = pv.varient_id
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .FirstOrDefault();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
             throw ex;
        }
    }

    public async Task<List<ProductResponseDto>> getProducts(
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .AsNoTracking()
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .OrderByDescending(pr => pr.create_at)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategory_id = pr.subcategory_id,
                    store_id = pr.store_id,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.product_id == pr.id)
                        .GroupBy(pv => pv.varient_id, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varient_id = pv.varient_id
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductResponseDto>();
        }
    }


    public async Task<List<ProductResponseDto>> getProducts(
        Guid storeId,
        Guid subCategory_id,
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .AsNoTracking()
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.store_id == storeId && pr.subcategory_id == subCategory_id)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategory_id = pr.subcategory_id,
                    store_id = pr.store_id,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.product_id == pr.id)
                        .GroupBy(pv => pv.varient_id, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varient_id = pv.varient_id
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductResponseDto>();
        }
    }


    public async Task<bool> isExist(
        Guid id)
    {
        try
        {
            return await _dbContext.Products.FindAsync(id) != null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return false;
        }
    }

    public async Task<bool> deleteProductVarient(
        List<ProductVarientRequestDto> productVarients
        ,Guid product_Id)
    {
        try
        {
            
            productVarients.ForEach((x) =>
            {
                var result =  _dbContext.ProductVarients.FirstOrDefault(pv =>
                    pv.product_id == product_Id && pv.varient_id == x.varient_id && pv.name == x.name);
               if(result!=null)
                   _dbContext.ProductVarients.Remove(result);

            });
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }
  public async Task<bool> deleteProductVarient(Guid product_Id)
    {
        try
        {
            var result = _dbContext.ProductVarients
                .Where(pv=>pv.product_id==product_Id);
            _dbContext.ProductVarients.RemoveRange(result);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }

    public async  Task<bool>deleteProductImages(List<string> productImage)
    {
        try {
            
            var result = _dbContext.ProductImages.Where(x => productImage.Contains(
                x.name));
            _dbContext.ProductImages.RemoveRange(result);
            clsUtil.deleteFile(productImage, _host);
            return true;
            
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }
   public async Task<bool> deleteProductImages(Guid product_id)
    {
        try
        {
            var result = _dbContext.ProductImages.Where(pv=>pv.productId==product_id);
            _dbContext.ProductImages.RemoveRange(result);
            clsUtil.deleteFile(result.Select(x=>x.name).ToList(), _host);
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }

    public async Task<bool>deleteProduct(Guid product_id)
    {
        try
        {
            var product = await _dbContext.Products.FirstOrDefaultAsync(pro=>pro.id==product_id);

            if (product == null) return false;
            
            _dbContext.Remove(product);
            await deleteProductImages(product_id);
            await deleteProductVarient(product_id);
            return true;
        }
        catch (Exception e)
        {
            Console.WriteLine("this error from delete product by id "+e.Message);
            return false;
        }
    }


    public async Task<List<ProductResponseDto>> getProducts(
        Guid storeId,
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .AsNoTracking()
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.store_id == storeId)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategory_id = pr.subcategory_id,
                    store_id = pr.store_id,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.product_id == pr.id)
                        
                        .GroupBy(pv => pv.varient_id, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varient_id = pv.varient_id
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductResponseDto>();
        }
    }


    public async Task<ProductResponseDto?> createProduct(
        string name,
        string description,
        string thumbnail,
        Guid subcategory_id,
        Guid store_id,
        decimal price,
        List<string> images,
        List<ProductVarientRequestDto>? productVarients=null
    )
    {
        try
        {
            var id = clsUtil.generateGuid();
            var productCreationResult = _dbContext.Products.AddAsync(new Product
            {
                id = id,
                name = name,
                description = description,
                subcategory_id = subcategory_id,
                store_id = store_id,
                price = price,
                create_at = DateTime.Now,
                update_at = null,
                thmbnail = thumbnail
            });

            if(productVarients!=null)
            productVarients.ForEach(x =>
                _dbContext.ProductVarients.AddAsync(new ProductVarient
                {
                    id = clsUtil.generateGuid(),
                    name = x.name,
                    precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                    varient_id = x.varient_id,
                    product_id = id
                })
            );
            images.ForEach(x =>
                _dbContext.ProductImages.AddAsync(new ProductImage
                {
                    ID = clsUtil.generateGuid(),
                    name = x,
                    productId = id
                })
            );
            await _dbContext.SaveChangesAsync();
            return  getProduct(id);
        }
        catch (Exception ex)
        {
            clsUtil.deleteFile(thumbnail, _host);
            images.ForEach(x => clsUtil.deleteFile(x, _host));

            return null;
        }
    }

    
    public async Task<ProductResponseDto?> updateProduct(
        Guid id,
        string? name,
        string? description,
        string? thumbnail,
        Guid? subcategory_id,
        decimal? price,
        List<ProductVarientRequestDto>? productVarients,
        List<string>? images
    )
    {
        try
        {
            var product = await _dbContext.Products.FindAsync(id);

            if (product == null) return null;

            product.name = name ?? product.name;
            product.description = description ?? product.description;
            product.subcategory_id = subcategory_id ?? product.subcategory_id;
            product.price = price ?? product.price;
            product.thmbnail = thumbnail ?? product.thmbnail;
            product.update_at = DateTime.Now;

            if (productVarients != null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        id = clsUtil.generateGuid(),
                        name = x.name,
                        precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                        varient_id = x.varient_id,
                        product_id = id
                    })
                );
            if (images != null)
                images.ForEach(x =>
                    _dbContext.ProductImages.AddAsync(new ProductImage
                    {
                        ID = clsUtil.generateGuid(),
                        name = x,
                        productId = id
                    })
                );
            await _dbContext.SaveChangesAsync();
            return  getProduct(id);
        }
        catch (Exception ex)
        {
            if (thumbnail != null)
                clsUtil.deleteFile(thumbnail, _host);

            if (images != null)
                images.ForEach(x => clsUtil.deleteFile(x, _host));

            return null;
        }
    }
}