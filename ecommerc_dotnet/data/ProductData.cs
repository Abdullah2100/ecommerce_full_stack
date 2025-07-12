using ecommerc_dotnet.context;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
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
            return _dbContext.Products
                .AsNoTracking()
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.id == id)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategoryId = pr.subcategoryId,
                    storeId = pr.storeId,
                    price = pr.price,
                    categoryId = pr.subCategory.categoriId,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientId = pv.varientId
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
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(pr => pr.createdAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategoryId = pr.subcategoryId,
                    storeId = pr.storeId,
                    price = pr.price,
                    categoryId = pr.subCategory.categoriId,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientId = pv.varientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductResponseDto>();
        }
    }
    public async Task<List<ProductsResponseAdminDto>> getProductsAdmin(
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .Include(pro => pro.productImages)
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.productVarients)
                .Include(pro => pro.store)
                .AsNoTracking()
                .AsSplitQuery()
                .OrderByDescending(pr => pr.createdAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductsResponseAdminDto() 
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategory = pr.subCategory.name,
                    store = pr.store.name,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResonseAdminDto
                                {
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientName = _dbContext.Varients.FirstOrDefault(var=>var.id==(Guid)pv.varientId!)!.name
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductsResponseAdminDto>();
        }
    }

  public async Task<int> getProduct()
    {
        try
        {
            var result = await _dbContext.Products
                .AsNoTracking()
                .CountAsync();
            if (result == 0) return 0;
            return (int)Math.Ceiling((double)result / 25);
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return 0;
        }
    }


    public async Task<List<ProductResponseDto>> getProducts(
        Guid storeId,
        Guid subcategoryId,
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.storeId == storeId && pr.subcategoryId == subcategoryId)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategoryId = pr.subcategoryId,
                    storeId = pr.storeId,
                    price = pr.price,
                    categoryId = pr.subCategory.categoriId,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientId = pv.varientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
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
            return await _dbContext.Products.FindAsync(id) !=  null;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return false;
        }
    }

    public async Task<bool> deleteProductVarient(
        List<ProductVarientRequestDto> productVarients
        , Guid productId)
    {
        try
        {
            productVarients.ForEach( (x) =>
            {
               _dbContext.ProductVarients.Where(pv =>
                    pv.productId == productId && pv.varientId == x.varientId && pv.name == x.name)
                     .ExecuteDelete();
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

    public async Task<bool> deleteProductVarient(Guid productId)
    {
        try
        {
           await _dbContext.ProductVarients
                .Where(pv => pv.productId == productId)
                .ExecuteDeleteAsync();
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }

    public async Task<bool> deleteProductImages(List<string> productImage)
    {
        try
        {
            await _dbContext
                .ProductImages
                .Where(x => productImage.Contains(
                x.name))
                .ExecuteDeleteAsync();
            clsUtil.deleteFile(productImage, _host);
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }

    public bool deleteProductImages(Guid productId)
    {
        try
        {
            var result = _dbContext
                .ProductImages
                .Where(pv => pv.productId == productId);
            _dbContext.ProductImages.RemoveRange(result);
            clsUtil.deleteFile(result.Select(x => x.name).ToList(), _host);
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from delete productVarient " + ex.Message);
            return false;
        }
    }

    public async Task<bool> deleteProduct(Guid productId)
    {
        try
        {
             await _dbContext
                 .Products
                 .Where(pro => pro.id == productId)
                 .ExecuteDeleteAsync();

             deleteProductImages(productId);
            await deleteProductVarient(productId);
            return true;
        }
        catch (Exception e)
        {
            Console.WriteLine("this error from delete product by id " + e.Message);
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
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.storeId == storeId)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategoryId = pr.subcategoryId,
                    storeId = pr.storeId,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientId = pv.varientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return new List<ProductResponseDto>();
        }
    }
   public async Task<List<ProductResponseDto>?> getProductsByCategory(
        Guid category_Id,
        int pageNumber,
        int pageSize = 25)
    {
        try
        {
            return await _dbContext.Products
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.productImages)
                .Include(pro => pro.productVarients)
                .Where(pr => pr.subCategory.categoriId==category_Id)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.id,
                    name = pr.name,
                    description = pr.description,
                    thmbnail = _config.getKey("url_file") + pr.thmbnail,
                    subcategoryId = pr.subcategoryId,
                    storeId = pr.storeId,
                    price = pr.price,
                    productVarients = pr.productVarients
                        .Where(pv => pv.productId == pr.id)
                        .GroupBy(pv => pv.varientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.id,
                                    name = pv.name,
                                    precentage = pv.precentage,
                                    varientId = pv.varientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.productImages.Select(pi => _config.getKey("url_file") + pi.name).ToList()
                })
                .ToListAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine("this error occured from getting product " + ex.Message);
            return null;
        }
    }



    public async Task<ProductResponseDto?> createProduct(
        string name,
        string description,
        string thumbnail,
        Guid subcategoryId,
        Guid storeId,
        decimal price,
        List<string> images,
        List<ProductVarientRequestDto>? productVarients = null
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
                subcategoryId = subcategoryId,
                storeId = storeId,
                price = price,
                createdAt = DateTime.Now,
                updatedAt = null,
                thmbnail = thumbnail
            });

            if (productVarients !=  null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        id = clsUtil.generateGuid(),
                        name = x.name,
                        precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                        varientId = x.varientId,
                        productId = id
                    })
                );
            images.ForEach(x =>
                _dbContext.ProductImages.AddAsync(new ProductImage
                {
                    id = clsUtil.generateGuid(),
                    name = x,
                    productId = id
                })
            );
            await _dbContext.SaveChangesAsync();
            return getProduct(id);
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
        Guid? subcategoryId,
        decimal? price,
        List<ProductVarientRequestDto>? productVarients,
        List<string>? images
    )
    {
        try
        {
            var product = await _dbContext.Products.FindAsync(id);

            if (product is null) return null;

            product.name = name ?? product.name;
            product.description = description ?? product.description;
            product.subcategoryId = subcategoryId ?? product.subcategoryId;
            product.price = price ?? product.price;
            product.thmbnail = thumbnail ?? product.thmbnail;
            product.updatedAt = DateTime.Now;

            if (productVarients !=  null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        id = clsUtil.generateGuid(),
                        name = x.name,
                        precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                        varientId = x.varientId,
                        productId = id
                    })
                );
            if (images !=  null)
                images.ForEach(x =>
                    _dbContext.ProductImages.AddAsync(new ProductImage
                    {
                        id = clsUtil.generateGuid(),
                        name = x,
                        productId = id
                    })
                );
            await _dbContext.SaveChangesAsync();
            return getProduct(id);
        }
        catch (Exception _)
        {
            if (thumbnail !=  null)
                clsUtil.deleteFile(thumbnail, _host);

            if (images !=  null)
                images.ForEach(x => clsUtil.deleteFile(x, _host));

            return null;
        }
    }
}