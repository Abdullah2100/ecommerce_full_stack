using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.infrastructure.repositories;

namespace ecommerc_dotnet.application.UnitOfWork;

public class UnitOfWork : IUnitOfWork
{
    private readonly AppDbContext _context;

    public UnitOfWork(
        AppDbContext context
    )
    {
        _context = context;
        AddressRepository= new AddressRepository( _context );
        BannerRepository = new BannerRepository( _context );
        CategoryRepository = new CategoryRepository( _context );
        DeliveryRepository = new DeliveryRepository( _context );
        GeneralSettingRepository = new GeneralSettingRepository( _context );
        OrderItemRepository = new OrderItemRepository( _context );
        ProductVariantRepository =  new ProductVariantRepository( _context );
        ProductImageRepository = new ProductImageRepository( _context );
        ProductRepository = new ProductRepository(context);
        OrderRepository = new OrderRepository( _context);
        PasswordRepository = new ReseatePasswordRepository( _context );
        StoreRepository = new StoreRepository( _context );
        SubCategoryRepository = new SubCategoryRepository( _context );
        UserRepository = new UserRepository( _context );
        VarientRepository =  new VarientRepository( _context );
        OrderProductVariantRepository = new OrderProductVariantRepository(context);

    }
    
    public void Dispose()
    {
        _context.Dispose();
    }

    public IOrderProductVariant OrderProductVariantRepository { get; }

    public async Task<int> saveChanges()
    {
        try
        {
            return await _context.SaveChangesAsync();

        }
        catch (Exception ex)
        {
            Console.WriteLine($"this the exption error {ex.Message}");
            return 0;
        }
    }

    public IAddressRepository AddressRepository { get; }
    public IBannerRepository BannerRepository { get; }
    public ICategoryRepository CategoryRepository { get; }
    public IDeliveryRepository DeliveryRepository { get; }
    public IGeneralSettingRepository GeneralSettingRepository { get; }
    public IOrderItemRepository OrderItemRepository { get; }
    public IOrderRepository OrderRepository { get; }
    public IProductRepository ProductRepository { get; }
    public IProductImageRepository ProductImageRepository { get; }
    public IProductVariantRepository ProductVariantRepository { get; }
    public IReseatePasswordRepository PasswordRepository { get; }
    public IStoreRepository StoreRepository { get; }
    public ISubCategoryRepository SubCategoryRepository { get; }
    public IUserRepository UserRepository { get; }
    public IVarientRepository VarientRepository { get; }
}