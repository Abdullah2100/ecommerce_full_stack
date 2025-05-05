using ecommerc_dotnet.module;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.context;

public class AppDbContext:DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }
    
    public DbSet<User> Users { get; set; }
   protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
     
       
            modelBuilder.Entity<User>(
                user =>
                {
                    user.Property(p => p.role)
                        .HasColumnType("int")
                        .HasDefaultValue(1);

                    user.Property(p => p.created_at)
                        .HasColumnType("timestamp")
                        .HasDefaultValueSql("CURRENT_TIMESTAMP");

                    user.Property(p => p.updated_at)
                        .HasColumnType("timestamp")
                        .HasDefaultValueSql("NULL");

                    user.OwnsOne
                        (u => u.person, person =>
                        {
                            person.Property(p => p.name)
                                .IsRequired()
                                .HasMaxLength(50);

                            person.Property(p => p.phone)
                                .IsRequired()
                                .HasMaxLength(13);
        
                            person.Property(p=>p.address)
                                .IsRequired();
            
                            person.Property(p=>p.email)
                                .IsRequired();
                            
                            person.HasIndex(p => new { p.email, p.phone }).IsUnique();
                        });
                }
            );

        base.OnModelCreating(modelBuilder);
    }
}