using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.context;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    
    public DbSet<ReseatePasswordOtp> ReseatPasswords { get; set; }
    
    public DbSet<Address> Address { get; set; }
    
    public DbSet<Store> Stores { get; set; }
    public DbSet<Category> Categories { get; set; }
    public DbSet<SubCategory> SubCategories { get; set; }

    public DbSet<Bannel> Banner { get; set; }
    
    public DbSet<Varient> Varients { get; set; }
    
    public DbSet<Product> Products { get; set; }
    public DbSet<ProductVarient> ProductVarients { get; set; }
    public DbSet<ProductImage> ProductImages { get; set; }
    public DbSet<Order> Orders { get; set; }
    public DbSet<OrderItem> OrderItems { get; set; }
    public DbSet<OrderProductsVarient> OrdersProductsVarients { get; set; }
    public DbSet<GeneralSettings> GeneralSettings { get; set; }
    
    public DbSet<Delivery> Deliveries { get; set; }
    
    

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
      
        modelBuilder.Entity<User>(
            user =>
            {
             
                user.HasIndex(u => new { u.email, u.phone }).IsUnique();

                user.HasMany(ca=>ca.categories)
                    .WithOne(u => u.user)
                    .HasForeignKey(c=>c.ownerId)
                    .HasPrincipalKey(u=>u.id)
                    .OnDelete(DeleteBehavior.Restrict);
                
                user.HasOne(u => u.store)
                    .WithOne(st => st.user)
                    .HasForeignKey<Store>(st=>st.userId)
                    .HasPrincipalKey<User>(u=>u.id)
                    .OnDelete(DeleteBehavior.Restrict);
            }
        );

        modelBuilder.Entity<Delivery>(delev =>
        {
            delev.HasOne(de => de.user)
                .WithOne(u => u.delivery)
                .HasForeignKey<Delivery>(de => de.userId)
                .HasPrincipalKey<User>(u => u.id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Category>(ca =>
        {
            ca.HasIndex(c => new { c.name }).IsUnique();

            ca.HasMany(cat => cat.subCategories)
                .WithOne(sub=>sub.category)
                .HasForeignKey(sub=>sub.categoriId)
                .HasPrincipalKey(cat=>cat.id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Store>(st =>
        {
            st.HasIndex(s => s.name).IsUnique();
            
            st.HasMany(std=>std.SubCategories)
                .WithOne(sub=>sub.Store)
                .HasForeignKey(sub=>sub.storeId)
                .HasPrincipalKey(stc=>stc.id)
                .OnDelete(DeleteBehavior.Restrict);
            
            st.HasMany(sto => sto.banners)
                .WithOne(bn=>bn.store)
                .HasForeignKey(ban=>ban.storeId)
                .HasPrincipalKey(sto=>sto.id)
                .OnDelete(DeleteBehavior.Restrict);
            
        });

        modelBuilder.Entity<SubCategory>(sub =>
        {
        });

        modelBuilder.Entity<Bannel>(ba =>
        {
        });

        modelBuilder.Entity<Product>(pr =>
        {
            pr.HasMany<ProductVarient>(pro => pro.productVarients)
                .WithOne(p => p.product)
                .HasForeignKey(p => p.productId)
                .HasPrincipalKey(pro => pro.id)
                .OnDelete(DeleteBehavior.Restrict);
            pr.HasOne<SubCategory>(pro=>pro.subCategory) 
                .WithMany(sub=>sub.products)
                .HasForeignKey(pro=>pro.subcategoryId)
                .HasPrincipalKey(sub => sub.id)
                .OnDelete(DeleteBehavior.Restrict);
            pr.HasOne(pro=>pro.store)
                .WithMany(st=>st.products)
                .HasForeignKey(pro=>pro.storeId)
                .HasPrincipalKey(str=>str.id)
                .OnDelete(DeleteBehavior.Restrict);
            
            pr.HasMany<ProductImage>(pro => pro.productImages)
                .WithOne(pim=>pim.product)
                .HasForeignKey(pim=>pim.productId)
                .HasPrincipalKey(pro=>pro.id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Varient>(va =>
        {
            va.HasIndex(var=>var.name).IsUnique();
            va.HasMany(var=>var.productVarients)
                .WithOne(var=>var.varient)
                .HasForeignKey(var=>var.varientId)
                .HasPrincipalKey(var=>var.id)
                .OnDelete(DeleteBehavior.Restrict);
        });
        
        modelBuilder.Entity<Order>(or =>
        {
            or.HasOne<User>(ord => ord.user)
                .WithMany(u => u.orders)
                .HasForeignKey(ord => ord.userId)
                .HasPrincipalKey(u => u.id);
            
            or.HasMany(ord => ord.items)
                .WithOne(orIt => orIt.order)
                .HasForeignKey(ord => ord.orderId)
                .HasPrincipalKey(orIt => orIt.id);
        });
       
        modelBuilder.Entity<OrderItem>(oIt =>
        {
            
            oIt.HasOne(orIt => orIt.store)
                .WithMany(st=>st.oddrderItems)
                .HasForeignKey(orIt => orIt.storeId)
                .HasPrincipalKey(st => st.id);
            
            oIt.HasMany(orIt => orIt.orderProductsVarients)
                .WithOne(opv => opv.orderItem)
                .HasForeignKey(orIt => orIt.orderItemId)
                .HasPrincipalKey(orIt => orIt.id);
        });
        
        modelBuilder.Entity<OrderProductsVarient>(opv =>
        {
            opv.HasOne(OPV => OPV.productVarient)
                .WithMany(or => or.orderProductsVarients)
                .HasForeignKey(or => or.productVarientId)
                .HasPrincipalKey(or => or.id);


        }); 

        base.OnModelCreating(modelBuilder);
    }
}