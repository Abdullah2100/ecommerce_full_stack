using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.entity;
using ecommerc_dotnet.module;
using Microsoft.VisualStudio.Web.CodeGenerators.Mvc.Templates.BlazorIdentity.Pages.Manage;

namespace ecommerc_dotnet.UnitOfWork;

public interface IUnitOfWork :IDisposable
{
    IRepository<Address> AddressRepository { get; }
    IRepository<Banner>  BannelRepository { get; }
    IRepository<Category> CategoryRepository { get; }
    IRepository<Delivery> DeliveryRepository { get; }
    IRepository<GeneralSetting> GeneralSettingsRepository { get; }
    IRepository<Order> OrderRepository { get; }
    IRepository<OrderItem> OrderItemRepository { get; }
    IRepository<OrderProductsVarient> OrderProductsVarientRepository { get; }
    IRepository<Product> ProductRepository { get; }
    IRepository<ProductImage> ProductImageRepository { get; }
    IRepository<ProductVarient> ProductVarientRepository { get; }
    IRepository<ReseatePasswordOtp> ReseatePasswordOtpRepository { get; }
    IRepository<Store> StoreRepository { get; }
    IRepository<SubCategory> SubCategoryRepository { get; }
    IRepository<User> UserRepository { get; }
    IRepository<Varient>  VarientRepository { get; }
    Task<int> Complate();
}