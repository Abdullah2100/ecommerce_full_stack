using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.context;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    
    public DbSet<ReseatePasswordOtp> ReseatPassword { get; set; }
    
    public DbSet<Address> Address { get; set; }
    
    public DbSet<Store> Store { get; set; }
    public DbSet<Category> Category { get; set; }
    public DbSet<SubCategory> SubCategory { get; set; }

    public DbSet<Bannel> Banner { get; set; }
    
    public DbSet<Varient> Varients { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
       
        modelBuilder.Entity<User>(
            user =>
            {
             
                user.HasIndex(u => new { u.email, u.phone }).IsUnique();;

                user.HasMany(ca=>ca.categories)
                    .WithOne(u => u.user)
                    .HasForeignKey(c=>c.owner_id)
                    .HasPrincipalKey(u=>u.ID);
                
                user.HasOne(u => u.Store)
                    .WithOne(st => st.user)
                    .HasForeignKey<Store>(st=>st.user_id)
                    .HasPrincipalKey<User>(u=>u.ID);
                
            }
        );

        modelBuilder.Entity<Category>(ca =>
        {
            ca.HasIndex(c => new { c.name }).IsUnique();

            ca.HasMany(cat => cat.subCategories)
                .WithOne(sub=>sub.category)
                .HasForeignKey(sub=>sub.categori_id)
                .HasPrincipalKey(cat=>cat.id);
        });

        modelBuilder.Entity<Store>(st =>
        {
            st.HasIndex(s => s.name).IsUnique();
            
            st.HasMany(std=>std.SubCategories)
                .WithOne(sub=>sub.Store)
                .HasForeignKey(sub=>sub.store_id)
                .HasPrincipalKey(stc=>stc.id);
            
            st.HasMany(sto => sto.banners)
                .WithOne(bn=>bn.store)
                .HasForeignKey(ban=>ban.store_id)
                .HasPrincipalKey(sto=>sto.id);
            
        });

        modelBuilder.Entity<SubCategory>(sub =>
        {
        });

        modelBuilder.Entity<Bannel>(ba =>
        {
        });

        modelBuilder.Entity<Varient>(va =>
        {
            va.HasIndex(var=>var.name).IsUnique();
        });
        
        base.OnModelCreating(modelBuilder);
    }
}