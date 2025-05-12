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

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
       
        modelBuilder.Entity<User>(
            user =>
            {
             
                user.HasIndex(u => new { u.email, u.phone }).IsUnique();;

                user.HasMany<Address>(e => e.addresses)
                    .WithOne(u => u.owner)
                    .HasForeignKey(u => u.owner_id)
                    .HasPrincipalKey(u => u.ID);
                
                user.HasMany<Category>()
                    .WithOne(u => u.user)
                    .HasForeignKey(c=>c.owner_id)
                    .HasPrincipalKey(u=>u.ID);
            }
        );

        modelBuilder.Entity<Category>(ca =>
        {

            ca.HasIndex(c => new { c.name }).IsUnique();
            ;
        });
        base.OnModelCreating(modelBuilder);
    }
}