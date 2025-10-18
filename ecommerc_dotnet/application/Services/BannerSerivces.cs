using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.shared.extentions;
using ecommerc_dotnet.shared.signalr;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.application.Services;

public class BannerSerivces(
    IConfig config,
    IWebHostEnvironment host,
    IHubContext<BannerHub> hubContext,
    IUnitOfWork unitOfWork,
    IFileServices fileServices)
    : IBannerSerivces
{
    public async Task<Result<BannerDto?>> createBanner(
        Guid userId,
        CreateBannerDto bannerDto
    )
    {
        User? user = await unitOfWork.UserRepository
            .getUser(userId);
        var validation = user.isValidateFunc(false, true);
        if (validation is not null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }


        string? image = await fileServices.saveFile(
            bannerDto.Image,
            EnImageType.BANNER);

        if (image is null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while saving banner  image",
                isSuccessful: false,
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

        unitOfWork.BannerRepository.add(banner);
        var result = await unitOfWork.saveChanges();
        if (result == 0)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while adding new banner",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("createdBanner", result);

        return new Result<BannerDto?>
        (
            data: banner.toDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<bool>> deleteBanner(Guid id, Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(userId);

        var validation = user.isValidateFunc(false, true);
        if (validation is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }


        Banner? banner = await unitOfWork.BannerRepository
            .getBanner(id);


        if (banner is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "banner  not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (banner.StoreId != user.Store.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "only store owner can delete banner",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.BannerRepository.deleteAsync(id);
        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while deleting banner",
                isSuccessful: false,
                statusCode: 400
            );
        }

        fileServices.deleteFile(banner.Image);

        await hubContext.Clients.All.SendAsync("deletedOrder", id);


        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<BannerDto>>> getBannersAll(
        Guid adminId,
        int pageNumber,
        int pageSize)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);
        var validation = user.isValidateFunc();
        if (validation is not null)
        {
            return new Result<List<BannerDto>>
            (
                data: new List<BannerDto>(),
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }
        
      
        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .getBanners(pageNumber, pageSize))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<BannerDto>>> getBanners(
        Guid storeId,
        int pageNumber,
        int pageSize
    )
    {
        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .getBanners(storeId, pageNumber, pageSize))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<BannerDto>>> getBanners(
        int randomLenght
    )
    {
        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .getBanners(randomLenght))
            .Select(ba => ba.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}