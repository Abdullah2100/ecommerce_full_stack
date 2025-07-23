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
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVarients)
                .Where(pr => pr.Id == id)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategoryId = pr.SubcategoryId,
                    storeId = pr.StoreId,
                    price = pr.Price,
                    categoryId = pr.subCategory.CategoryId,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.Id,
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientId = pv.VarientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVarients)
                .AsSplitQuery()
                .AsNoTracking()
                .OrderByDescending(pr => pr.CreatedAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategoryId = pr.SubcategoryId,
                    storeId = pr.StoreId,
                    price = pr.Price,
                    categoryId = pr.subCategory.CategoryId,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.Id,
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientId = pv.VarientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                .Include(pro => pro.ProductImages)
                .Include(pro=>pro.subCategory)
                .Include(pro => pro.ProductVarients)
                .Include(pro => pro.store)
                .AsNoTracking()
                .AsSplitQuery()
                .OrderByDescending(pr => pr.CreatedAt)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductsResponseAdminDto() 
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategory = pr.subCategory.Name,
                    store = pr.store.Name,
                    price = pr.Price,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResonseAdminDto
                                {
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientName = _dbContext.Varients.FirstOrDefault(var=>var.Id==(Guid)pv.VarientId!)!.Name
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVarients)
                .Where(pr => pr.StoreId == storeId && pr.SubcategoryId == subcategoryId)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategoryId = pr.SubcategoryId,
                    storeId = pr.StoreId,
                    price = pr.Price,
                    categoryId = pr.subCategory.CategoryId,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.Id,
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientId = pv.VarientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                    pv.ProductId == productId && pv.VarientId == x.varientId && pv.Name == x.name)
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
                .Where(pv => pv.ProductId == productId)
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
                x.Path))
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
                .Where(pv => pv.ProductId == productId);
            _dbContext.ProductImages.RemoveRange(result);
            clsUtil.deleteFile(result.Select(x => x.Path).ToList(), _host);
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
                 .Where(pro => pro.Id == productId)
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
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVarients)
                .Where(pr => pr.StoreId == storeId)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategoryId = pr.SubcategoryId,
                    storeId = pr.StoreId,
                    price = pr.Price,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.Id,
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientId = pv.VarientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVarients)
                .Where(pr => pr.subCategory.CategoryId==category_Id)
                .AsNoTracking()
                .AsSplitQuery()
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(pr => new ProductResponseDto
                {
                    id = pr.Id,
                    name = pr.Name,
                    description = pr.Description,
                    thmbnail = _config.getKey("url_file") + pr.Thmbnail,
                    subcategoryId = pr.SubcategoryId,
                    storeId = pr.StoreId,
                    price = pr.Price,
                    productVarients = pr.ProductVarients
                        .Where(pv => pv.ProductId == pr.Id)
                        .GroupBy(pv => pv.VarientId, (key, g)
                            => g.Select(
                                pv => new ProductVarientResponseDto
                                {
                                    id = pv.Id,
                                    name = pv.Name,
                                    precentage = pv.Precentage,
                                    varientId = pv.VarientId
                                }
                            ).ToList()
                        ).ToList(),
                    productImages = pr.ProductImages.Select(pi => _config.getKey("url_file") + pi.Path).ToList()
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
                Id = id,
                Name = name,
                Description = description,
                SubcategoryId = subcategoryId,
                StoreId = storeId,
                Price = price,
                CreatedAt = DateTime.Now,
                UpdatedAt = null,
                Thmbnail = thumbnail
            });

            if (productVarients !=  null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Name = x.name,
                        Precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                        VarientId = x.varientId,
                        ProductId = id
                    })
                );
            images.ForEach(x =>
                _dbContext.ProductImages.AddAsync(new ProductImage
                {
                    Id = clsUtil.generateGuid(),
                    Path = x,
                    ProductId = id
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

            product.Name = name ?? product.Name;
            product.Description = description ?? product.Description;
            product.SubcategoryId = subcategoryId ?? product.SubcategoryId;
            product.Price = price ?? product.Price;
            product.Thmbnail = thumbnail ?? product.Thmbnail;
            product.UpdatedAt = DateTime.Now;

            if (productVarients !=  null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Name = x.name,
                        Precentage = x.precentage == 0 ? 1 : (decimal)x.precentage!,
                        VarientId = x.varientId,
                        ProductId = id
                    })
                );
            if (images !=  null)
                images.ForEach(x =>
                    _dbContext.ProductImages.AddAsync(new ProductImage
                    {
                        Id = clsUtil.generateGuid(),
                        Path = x,
                        ProductId = id
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