using System.Text;
using ecommerc_dotnet.context;
using ecommerc_dotnet.midleware.ConfigImplment;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using hotel_api.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();

var fireBaseConfig = Path.Combine(
    Directory.GetCurrentDirectory(), 
    "firebase-adminsdk.json"
    );

var firebaseCredential = GoogleCredential.FromFile(fireBaseConfig);
FirebaseApp.Create(new AppOptions()
{
    Credential = firebaseCredential
});

// Other Services (Scoped)
builder.Services.AddSingleton<IConfig, ConfigurationImplement>();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAllOrigins", policy =>
    {
        policy
.WithOrigins("http://localhost:3000")
        .AllowAnyOrigin()    // Allows all origins
            .AllowAnyMethod()    // Allows any HTTP methods (GET, POST, etc.)
            .AllowAnyHeader();
            // ;   // Allows any headers
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
app.MapHub<EcommercHub>("/bannerHub");
app.Run();