using System.Text;
using ecommerc_dotnet.context;
using hotel_api.Services;
using hotel_api.Services.EmailServices;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();



// Other Services (Scoped)
// builder.Services.AddTransient<IWebHostEnvironment>();
builder.Services.AddSingleton<IConfigurationServices, ConfigurationServicesImp>();
builder.Services.AddSingleton<IEmailServices, EmailServicesImplement>();



builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

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

// Middleware Pipeline
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

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
app.Run();