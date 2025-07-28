using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class ProductData
{
    public ProductData(
        AppDbContext dbContext,
        IConfig config,
        IWebHostEnvironment host,
        IUnitOfWork unitOfWork
    )
    {
        _dbContext = dbContext;
        _config = config;
        _host = host;
        _unitOfWork = unitOfWork;
    }

    private readonly AppDbContext _dbContext;
    private readonly IConfig _config;
    private readonly IWebHostEnvironment _host;
    private readonly IUnitOfWork _unitOfWork;

    public ProductDto? getProduct(Guid id)
    {
        return _dbContext.Products
            .AsNoTracking()
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .AsSplitQuery()
            .Where(pr => pr.Id == id)
            .Select(pr => pr.toDto(_config.getKey("url_file")))
            .FirstOrDefault();
    }

    public async Task<List<ProductDto>?> getProducts(
        int pageNumber,
        int pageSize = 25)
    {
        return (await _unitOfWork
                .ProductRepository
                .getAllAsync(pageNumber, pageSize,
                    ["SubCategory", "ProductImages", "ProductVarient"]))
            .Select(pr => pr.toDto(_config.getKey("url_file")))
            ?.ToList();
    }

    public async Task<List<AdminProductsDto>> getProductsAdmin(
        int pageNumber,
        int pageSize = 25)
    {
        return (await _unitOfWork
                .ProductRepository
                .getAllAsync(pageNumber, pageSize,
                    ["SubCategory", "ProductImages", "ProductVarient"]))
            .Select(pr => pr.toAdminDto(_config.getKey("url_file")))
            .ToList();
    }

    public async Task<int> getProduct()
    {
        var result = await _dbContext.Products
            .AsNoTracking()
            .CountAsync();
        if (result == 0) return 0;
        return (int)Math.Ceiling((double)result / 25);
    }


    public async Task<List<ProductDto>> getProducts(
        Guid storeId,
        Guid subcategoryId,
        int pageNumber,
        int pageSize = 25)
    {
        return await _dbContext.Products
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .Where(pr => pr.StoreId == storeId && pr.SubcategoryId == subcategoryId)
            .AsNoTracking()
            .AsSplitQuery()
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .Select(pr => pr.toDto(_config.getKey("url_file")))
            .ToListAsync();
    }


    public async Task<bool> isExist(
        Guid id)
    {
        return await _dbContext.Products.FindAsync(id) != null;
    }

  
    public async Task<bool> deleteProductVarient(
        List<CreateProductVarientDto> productVarients
        , Guid productId)
    {
  
            productVarients.ForEach( (x) =>
            {
                _dbContext.ProductVarients
                    .Include(pi=>pi.VarientId)
                    .Where(pv =>
                        pv.ProductId == productId && pv.VarientId == x.VarientId && pv.Name == x.Name)
                    .ExecuteDelete();
            });
            await _dbContext.SaveChangesAsync();
            return true;
     
    }
    public async Task<bool> deleteProductVarient(Guid productId)
    {
        await _dbContext.ProductVarients
            .Where(pv => pv.ProductId == productId)
            .ExecuteDeleteAsync();
        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;
        return true;
    }

    public async Task<bool> deleteProductImages(List<string> productImage)
    {
        await _dbContext
            .ProductImages
            .Where(x => productImage.Contains(
                x.Path))
            .ExecuteDeleteAsync();
        int result = await _dbContext.SaveChangesAsync();
        if (result == 0) return false;

        clsUtil.deleteFile(productImage, _host);
        return true;
    }

    public bool deleteProductImages(Guid productId)
    {
        var result = _dbContext
            .ProductImages
            .Where(pv => pv.ProductId == productId);
        _dbContext.ProductImages.RemoveRange(result);

        clsUtil.deleteFile(result.Select(x => x.Path).ToList(), _host);

        return true;
    }


    public async Task<bool> deleteProduct(Guid productId)
    {
        await _dbContext
            .Products
            .Where(pro => pro.Id == productId)
            .ExecuteDeleteAsync();

        await deleteProductVarient(productId);
        deleteProductImages(productId);
        return true;
    }


    public async Task<List<ProductDto>> getProducts(
        Guid storeId,
        int pageNumber,
        int pageSize = 25)
    {
        return await _dbContext.Products
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .Where(pr => pr.StoreId == storeId)
            .AsNoTracking()
            .AsSplitQuery()
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .Select(pr => pr.toDto(_config.getKey("url_file")))
            .ToListAsync();
    }

    public async Task<List<ProductDto>?> getProductsByCategory(
        Guid categoryId,
        int pageNumber,
        int pageSize = 25)
    {
        return await _dbContext.Products
            .Include(pro => pro.subCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVarients)
            .Where(pr => pr.subCategory.CategoryId == categoryId)
            .AsNoTracking()
            .AsSplitQuery()
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .Select(pr => pr.toDto(_config.getKey("url_file")))
            .ToListAsync();
    }


    public async Task<ProductDto?> createProduct(
        string name,
        string description,
        string thumbnail,
        Guid subcategoryId,
        Guid storeId,
        decimal price,
        List<string> images,
        List<CreateProductVarientDto>? productVarients = null
    )
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

            if (productVarients != null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Precentage = x.Precentage == 0 ? 1 : (decimal)x.Precentage!,
                        VarientId = x.VarientId,
                        ProductId = id,
                        Name = x.Name
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


    public async Task<ProductDto?> updateProduct(
        Guid id,
        string? name,
        string? description,
        string? thumbnail,
        Guid? subcategoryId,
        decimal? price,
        List<CreateProductVarientDto>? productVarients,
        List<string>? images
    )
    {
        
            var product = await _dbContext.Products.FindAsync(id);

            if (product is null) return null;

            product.Name = name ?? product.Name;
            product.Description = description ?? product.Description;
            product.SubcategoryId = subcategoryId ?? product.SubcategoryId;
            product.Price = price ?? product.Price;
            product.Thmbnail = thumbnail ?? product.Thmbnail;
            product.UpdatedAt = DateTime.Now;

            if (productVarients != null)
                productVarients.ForEach(x =>
                    _dbContext.ProductVarients.AddAsync(new ProductVarient
                    {
                        Id = clsUtil.generateGuid(),
                        Precentage = x.Precentage == 0 ? 1 : (decimal)x.Precentage!,
                        VarientId = x.VarientId,
                        ProductId = id,
                        Name = x.Name
                    })
                );
            if (images != null)
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
}