using ecommerc_dotnet.context;
using ecommerc_dotnet.di.Repository;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.UnitOfWork;

public class UnitOfWork:IUnitOfWork
{
    public UnitOfWork(AppDbContext context)
    {
        _context = context;
        AddressRepository = new Repository<Address>(context);
        BannelRepository = new Repository<Bannel>(context);
        CategoryRepository = new Repository<Category>(context);
        DeliveryRepository = new Repository<Delivery>(context);
        GeneralSettingsRepository = new Repository<GeneralSettings>(context);
        OrderRepository = new Repository<Order>(context);
        OrderItemRepository = new Repository<OrderItem>(context);
        OrderProductsVarientRepository = new Repository<OrderProductsVarient>(context);
        ProductRepository = new Repository<Product>(context);
        ProductImageRepository = new Repository<ProductImage>(context);
        ProductVarientRepository = new Repository<ProductVarient>(context);
        ReseatePasswordOtpRepository = new Repository<ReseatePasswordOtp>(context);
        StoreRepository = new Repository<Store>(context);
        SubCategoryRepository = new Repository<SubCategory>(context);
        UserRepository = new Repository<User>(context);
        VarientRepository = new Repository<Varient>(context);
    }

    private readonly AppDbContext  _context;
    
    public IRepository<Address> AddressRepository { get; }
    public IRepository<Bannel> BannelRepository { get; }
    public IRepository<Category> CategoryRepository { get; }
    public IRepository<Delivery> DeliveryRepository { get; }
    public IRepository<GeneralSettings> GeneralSettingsRepository { get; }
    public IRepository<Order> OrderRepository { get; }
    public IRepository<OrderItem> OrderItemRepository { get; }
    public IRepository<OrderProductsVarient> OrderProductsVarientRepository { get; }
    public IRepository<Product> ProductRepository { get; }
    public IRepository<ProductImage> ProductImageRepository { get; }
    public IRepository<ProductVarient> ProductVarientRepository { get; }
    public IRepository<ReseatePasswordOtp> ReseatePasswordOtpRepository { get; }
    public IRepository<Store> StoreRepository { get; }
    public IRepository<SubCategory> SubCategoryRepository { get; }
    public IRepository<User> UserRepository { get; }
    public IRepository<Varient> VarientRepository { get; }
     
    public async Task<int> Complate()
    {
      return await _context.SaveChangesAsync();
    }

    public void Dispose()
    {
        _context.Dispose();
    }
}