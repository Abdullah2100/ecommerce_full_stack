using System.Text;
using ecommerc_dotnet.context;
using hotel_api.Services;
using hotel_api.Services.EmailServices;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();


// Other Services (Scoped)
// builder.Services.AddTransient<IWebHostEnvironment>();
builder.Services.AddSingleton<IConfigurationServices, ConfigurationServicesImp>();
builder.Services.AddSingleton<IEmailServices, EmailServicesImplement>();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAllOrigins", policy =>
    {
        policy.AllowAnyOrigin()    // Allows all origins
            .AllowAnyMethod()    // Allows any HTTP methods (GET, POST, etc.)
            .AllowAnyHeader();   // Allows any headers
    });
});

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c => { c.SwaggerDoc("v1", new OpenApiInfo { Title = "My API", Version = "v1" }); });
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
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "My API v1");
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
app.Run();