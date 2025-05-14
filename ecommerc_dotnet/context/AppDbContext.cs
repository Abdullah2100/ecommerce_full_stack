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
    
    public DbSet<Category> Category { get; set; }
    public DbSet<Store> Store { get; set; }

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
                    .HasForeignKey<Store>(st=>st.user_id);
                
            }
        );

        modelBuilder.Entity<Category>(ca =>
        {
            ca.HasIndex(c => new { c.name }).IsUnique();
        });

        modelBuilder.Entity<Store>(st =>
        {
            st.HasIndex(s => s.name).IsUnique();
        });
        base.OnModelCreating(modelBuilder);
    }
}