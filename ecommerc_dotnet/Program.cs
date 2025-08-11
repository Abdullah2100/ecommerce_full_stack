using System.Text;
using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.context;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.data;
using ecommerc_dotnet.di.email;
using ecommerc_dotnet.infrastructure.repositories;
using ecommerc_dotnet.infrastructure.services;
using ecommerc_dotnet.midleware;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.services;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using hotel_api.Services.EmailService;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();


builder.Services.AddSingleton<IConfig, ConfigurationImplement>();
builder.Services.AddTransient<IEmail, Email>();

//respoitory 
builder.Services.AddTransient<IUserRepository, UserRepository>();
builder.Services.AddTransient<IReseatePasswordRepository, ReseatePasswordRepository>();
builder.Services.AddTransient<IAddressRepository, AddressRepository>();
builder.Services.AddTransient<IStoreRepository, StoreRepository>();
builder.Services.AddTransient<ICategoryRepository, CategoryRepository>();
builder.Services.AddTransient<ISubCategoryRepository, SubCategoryRepository>();
builder.Services.AddTransient<IVarientRepository, VarientRepository>();
builder.Services.AddTransient<IBannerRepository, BannerRepository>();
builder.Services.AddTransient<IGeneralSettingRepository,GeneralSettingRepository>();
builder.Services.AddTransient<IDeliveryRepository,DeliveryRepository>();
builder.Services.AddTransient<IProductRepository,ProductRepository>();
builder.Services.AddTransient<IOrderRepository,OrderRepository>();

//services
builder.Services.AddTransient<IUserServices, UserService>();
builder.Services.AddTransient<IStoreServices, StoreServices>();
builder.Services.AddTransient<ICategoryServices, CategoryServices>();
builder.Services.AddTransient<ISubCategoryServices, SubCategoryServices>();
builder.Services.AddTransient<IVarientServices, VarientServices>();
builder.Services.AddTransient<IBannerSerivces, BannerSerivces>();
builder.Services.AddTransient<IGeneralSettingServices, GeneralSettingServices>();
builder.Services.AddTransient<IDeliveryServices, DeliveryServices>();
builder.Services.AddTransient<IProductSerivces,ProductServices>();
builder.Services.AddTransient<IOrderServices,OrderServices>();





var fireBaseConfig = Path.Combine(
    Directory.GetCurrentDirectory(), 
    "firebase-adminsdk.json"
);

var firebaseCredential = GoogleCredential.FromFile(fireBaseConfig);
FirebaseApp.Create(new AppOptions()
{
    Credential = firebaseCredential
});

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAllOrigins", policy =>
    {
        policy
.WithOrigins("http://0.0.0.0:3000")
            .AllowAnyMethod()    // Allows any HTTP methods (GET, POST, etc.)
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
    options.UseNpgsql(connectionUrl, o => o.UseNetTopologySuite()));


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
app.MapHub<EcommerceHub>("/bannerHub");
app.ConfigureExceptionHandler();
app.Run();