using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared;
using ecommerc_dotnet.shared.signalr;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.infrastructure.services;

public class BannerSerivces(
    IConfig config,
    IWebHostEnvironment host,
    IHubContext<BannerHub> hubContext,
    IStoreRepository storeRepository,
    IBannerRepository bannerRepository,
    IUserRepository userRepository)
    : IBannerSerivces
{
    private readonly IStoreRepository _storeRepository = storeRepository;


    public async Task<Result<BannerDto?>> createBanner(
        Guid userId, 
        CreateBannerDto bannerDto
        )
    {
        User? user = await userRepository
            .getUser(userId);
        if (user is null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "user is blocked",
                isSeccessful: false,
                statusCode: 400
            );
        }
        
        if (user.Store is null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "user not has store",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Store.IsBlock)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "contact admin to remove the block from you store ",
                isSeccessful: false,
                statusCode: 400
            );
        }

        string? image = await clsUtil.saveFile(
            bannerDto.Image, 
            EnImageType.BANNER, 
            host);

        if (image is null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while saving banner  image",
                isSeccessful: false,
                statusCode: 400
            );
        }
        Banner banner = new Banner
        {
            Id = clsUtil.generateGuid(),
            EndAt = bannerDto.EndAt,
            CreatedAt = DateTime.Today,
            Image = image,
            StoreId = user.Store.Id,
        };
        
        int result = await bannerRepository.addAsync(banner);

        if (result == 0)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while adding new banner",
                isSeccessful: false,
                statusCode: 400
            );
        } 
        
        await hubContext.Clients.All.SendAsync("createdBanner", result);

        return new Result<BannerDto?>
        (
            data: banner.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true ,
            statusCode: 201
        ); 
        

    }

    public async Task<Result<bool>> deleteBanner(Guid id, Guid userId)
    {
        User? user = await userRepository
            .getUser(userId);
        if (user is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<bool>
            (
                data: false,
                message: "user is blocked",
                isSeccessful: false,
                statusCode: 400
            );
        }
        
        if (user.Store is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not has store",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Store.IsBlock)
        {
            return new Result<bool>
            (
                data: false,
                message: "contact admin to remove the block from you account ",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Banner? banner = await bannerRepository
            .getBanner(id);
        

        if (banner is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "banner  not found",
                isSeccessful: false,
                statusCode: 404
            );
        }
       
        if (banner.StoreId != user.Store.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "only store owner can delete banner",
                isSeccessful: false,
                statusCode: 404
            );
        }
       
        int result = await bannerRepository.deleteAsync(id);

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while deleting banner",
                isSeccessful: false,
                statusCode: 400
            );
        }

        clsUtil.deleteFile(banner.Image, host);
       
        await hubContext.Clients.All.SendAsync("deletedOrder", id);

        
        return new Result<bool>
        (
            data: true,
            message: "",
            isSeccessful: true ,
            statusCode: 204
        ); 
        

    }

    public async Task<Result<List<BannerDto>>> getBannersAll(
        Guid adminId,
        int pageNumber,
        int pageSize)
    {
        User? user = await userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<List<BannerDto>>
            (
                data: new List<BannerDto>(),
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role!=0 )
        {
         
            return new Result<List<BannerDto>>
            (
                data: new List<BannerDto>(),
                message: "not authorized to get banners",
                isSeccessful: false,
                statusCode: 400
            );
        }
        List<BannerDto> banners = (await bannerRepository
                .getBanners(pageNumber, pageSize))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();
        
        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSeccessful: true ,
            statusCode: 200
        ); 
    }

    public async Task<Result<List<BannerDto>>> getBanners(
        Guid storeId, 
        int pageNumber,
        int pageSize
        )
    {
        List<BannerDto> banners = (await bannerRepository
                .getBanners(storeId,pageNumber, pageSize))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();
        
        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSeccessful: true ,
            statusCode: 200
        ); 
    }
    public async Task<Result<List<BannerDto>>> getBanners(
        int randomLenght
    )
    {
        List<BannerDto> banners = (await bannerRepository
                .getBanners(randomLenght))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();
        
        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSeccessful: true ,
            statusCode: 200
        ); 
    }
}
