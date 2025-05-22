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

    public async Task<ProductResponseDto?> getProduct(Guid id)
    {
        try
        {
            return await _dbContext.Products
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
                .FirstOrDefaultAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return null;
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
                .OrderByDescending(pr=>pr.create_at)
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
                .Where(pr => pr.store_id == storeId &&pr.subcategory_id==subCategory_id)
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
        List<ProductVarientRequestDto> productVarients,
        List<string> images
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
            return await getProduct(id);
        }
        catch (Exception ex)
        {
            clsUtil.deleteFile(thumbnail, _host);
            images.ForEach(x => clsUtil.deleteFile(x, _host));

            return null;
        }
    }
}