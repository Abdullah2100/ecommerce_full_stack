using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.domain.Interface;

namespace ecommerc_dotnet.infrastructure;

public interface IUnitOfWork:IDisposable
{
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
    public IOrderProductVariant OrderProductVariantRepository { get; } 

    public Task<int> saveChanges();
}