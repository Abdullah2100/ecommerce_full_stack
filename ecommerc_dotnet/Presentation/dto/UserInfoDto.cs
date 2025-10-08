using System.ComponentModel.DataAnnotations;
using System.Text;
using ecommerc_dotnet.application;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.application.Services;
using ecommerc_dotnet.application.UnitOfWork;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.midleware;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.shared.signalr;
using hotel_api.Services.EmailService;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;


var  builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();


builder.Services.AddSingleton<IConfig, ConfigurationImplement>();
builder.Services.AddTransient<IEmail, Email>();

//unitofwork 
builder.Services.AddTransient<IUnitOfWork, UnitOfWork>();

//file services
builder.Services.AddTransient<FileServices,FileServices>();

//services
builder.Services.AddTransient<IUserServices, UserService>();
builder.Services.AddTransient<IStoreServices, StoreServices>();
builder.Services.AddTransient<ICategoryServices, CategoryServices>();
builder.Services.AddTransient<ISubCategoryServices, SubCategoryServices>();
builder.Services.AddTransient<IVarientServices, VarientServices>();
builder.Services.AddTransient<IBannerSerivces, BannerSerivces>();
builder.Services.AddTransient<IGeneralSettingServices, GeneralSettingServices>();
builder.Services.AddTransient<IDeliveryServices, DeliveryServices>();
builder.Services.AddTransient<IProductSerivces, ProductServices>();
builder.Services.AddTransient<IOrderServices, OrderServices>();
builder.Services.AddTransient<IOrderItemServices, OrderItemServices>();


// var fireBaseConfig = Path.Combine(
//     Directory.GetCurrentDirectory(), 
//     "firebase-adminsdk.json"
// );

// var firebaseCredential = GoogleCredential.FromFile(fireBaseConfig);
// FirebaseApp.Create(new AppOptions()
// {
//     Credential = firebaseCredential
// });


builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAllOrigins", policy =>
    {
        policy
            .WithOrigins("http://0.0.0.0:3000")
            .AllowAnyMethod() // Allows any HTTP methods (GET, POST, etc.)
            .AllowAnyHeader();
    });
});

builder.Services.AddControllers();
builder.Services.AddSignalR(option =>
    option.EnableDetailedErrors = true);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Authentication
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(configuration["credentials:key"] ?? "")
            ),
            ValidIssuer = configuration["credentials:Issuer"],
            ValidAudience = configuration["credentials:Audience"]
        };
    });

// Database
var connectionUrl = configuration["ConnectionStrings:connection_url"];
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionUrl));


var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.UseSwagger();
    app.UseSwaggerUI(options => // UseSwaggerUI is called only in Development.
    {
        options.SwaggerEndpoint("/swagger/v1/swagger.json", "v1");
        options.RoutePrefix = string.Empty;
    });
}

app.UseCors("AllowAllOrigins");

app.UseHttpsRedirection();

app.UseStaticFiles(new StaticFileOptions
{
    FileProvider = new PhysicalFileProvider(
        Path.Combine(builder.Environment.ContentRootPath, "images")),
    RequestPath = "/StaticFiles"
});
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();
app.MapHub<BannerHub>("/bannerHub");
app.MapHub<OrderHub>("/bannerHub");
app.MapHub<OrderItemHub>("/bannerHub");
app.MapHub<StoreHub>("/bannerHub");
app.ConfigureExceptionHandler();
app.Run();

namespace ecommerc_dotnet.dto
{
    public class UserInfoDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public bool IsAdmin { get; set; } = false;
        public string Phone { get; set; }
        public string Email { get; set; }
        public string StoreName { get; set; } = "";
        public bool IsActive { get; set; } = true;
        public string Thumbnail { get; set; }
        public List<AddressDto>? Address { get; set; }
        public Guid? StoreId { get; set; }
    }

    
    public class UserDeliveryInfoDto
    {
        public required string Name { get; set; } 
        public string Phone { get; set; }
        public string Email { get; set; }
        public string Thumbnail { get; set; }
    }

    public class UpdateUserInfoDto
    {
        [StringLength(maximumLength: 50 , ErrorMessage = "Enter Valide Name")]
        public string? Name { get; set; } = null;
        
        [StringLength(maximumLength: 13, ErrorMessage = "Enter Valide Name")]
        public string? Phone { get; set; } = null;
        public IFormFile? Thumbnail { get; set; } = null;
        public string? Password { get; set; } = null;
        public string? NewPassword { get; set; } = null;
       
    }
}