using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.domain.Interface;
using ecommerc_dotnet.application.Interface;
using ecommerc_dotnet.application.Result;
using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.Presentation.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.infrastructure;
using ecommerc_dotnet.shared.extentions;
using ecommerc_dotnet.shared.signalr;
using hotel_api.util;
using Microsoft.AspNetCore.SignalR;

namespace ecommerc_dotnet.application.Services;

public class StoreServices(
    IWebHostEnvironment host,
    IConfig config,
    IFileServices fileServices,
    IUnitOfWork unitOfWork,
    IHubContext<StoreHub> hubContext

)
    : IStoreServices
{
    private void deleteStoreImage(string? wallperper, string? smallImage)
    {
        if (wallperper is not null)
            fileServices.deleteFile(wallperper);
        if (smallImage is not null)
            fileServices.deleteFile(smallImage);
    }

    public async Task<Result<StoreDto?>> createStore(
        CreateStoreDto store,
        Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(userId);

        var isValide = user.isValidateFunc();

        if (isValide is not null)
        {
            return new Result<StoreDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        if (await unitOfWork.StoreRepository.isExist(store.Name))
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "store already exist",
                isSeccessful: false,
                statusCode: 400
            );
        }

        string? wallperper = null, smallImage = null;

        smallImage = await fileServices.saveFile(
            store.SmallImage,
            EnImageType.STORE);
        wallperper = await fileServices.saveFile(
            store.WallpaperImage,
            EnImageType.STORE);


        if (smallImage is null || wallperper is null)
        {
            deleteStoreImage(wallperper, smallImage);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while saving store images",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Guid id = clsUtil.generateGuid();
        Store? storeData = new Store
        {
            Id = id,
            Name = store.Name,
            WallpaperImage = wallperper,
            SmallImage = smallImage,
            IsBlock = true,
            UserId = userId,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
        };
        Address address = new Address
        {
            Id = clsUtil.generateGuid(),
            IsCurrent = true,
            Latitude = store.Latitude,
            Longitude = store.Longitude,
            Title = store.Name,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            OwnerId = id
        };

        unitOfWork.StoreRepository.add(storeData);


        unitOfWork.AddressRepository.add(address);


        int result = await unitOfWork.saveChanges();

        if (result == 0)
        {
            deleteStoreImage(wallperper, smallImage);
            fileServices.deleteFile([wallperper, smallImage]);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        storeData = await unitOfWork.StoreRepository.getStore(id)!;
        storeData!.Addresses = new List<Address> { address };


        return new Result<StoreDto?>
        (
            data: storeData?.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<StoreDto?>> updateStore(
        UpdateStoreDto storeDto,
        Guid userId
    )
    {
        if (storeDto.isEmpty())
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: true,
                statusCode: 200
            );
        }

        User? user = await unitOfWork.UserRepository
            .getUser(userId);

        var isValide = user.isValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<StoreDto?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        if (storeDto.Name is not null)
        {
            bool isExist = await unitOfWork.StoreRepository.isExist(storeDto.Name, user!.Store!.Id);

            if (isExist)
            {
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "store already exist",
                    isSeccessful: false,
                    statusCode: 400
                );
            }
        }


        string? wallperper = null, smallImage = null;

        if (storeDto.WallpaperImage is not null)
        {
            wallperper = await fileServices.saveFile(
                storeDto.WallpaperImage,
                EnImageType.STORE);

            deleteStoreImage(user!.Store!.WallpaperImage, null);
        }

        if (storeDto.SmallImage is not null)
        {
            smallImage = await fileServices.saveFile(
                storeDto.SmallImage,
                EnImageType.STORE);
            deleteStoreImage(null, user!.Store?.SmallImage);
        }

        user!.Store!.SmallImage = smallImage ?? user!.Store!.SmallImage;
        user!.Store!.WallpaperImage = wallperper ?? user!.Store!.WallpaperImage;
        user!.Store!.Name = storeDto.Name ?? user!.Store!.Name;
        user!.Store!.UpdatedAt = DateTime.Now;

        unitOfWork.StoreRepository.update(user!.Store!);

        if (
            (storeDto.Longitude is null && storeDto.Latitude is not null) ||
            (storeDto.Longitude is not null && storeDto.Latitude is null)
        )
        {
            return new Result<StoreDto?>(
                isSeccessful: false,
                data: null,
                message: "when update address you must change both longitude and latitude not one of them only ",
                statusCode: 400
            );
        }

        if (storeDto?.Longitude is not null && storeDto?.Latitude is not null)
        {
            Address? address = await unitOfWork.AddressRepository
                .getAddressByOwnerId(user!.Store!.Id);

            if (address is null)
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "store not has any address",
                    isSeccessful: false,
                    statusCode: 404
                );
            address.Title = storeDto?.Name ?? address.Title;
            address.UpdatedAt = DateTime.Now;
            address.Longitude = (decimal)storeDto?.Longitude!;
            address.Latitude = (decimal)storeDto!.Latitude;
            unitOfWork.AddressRepository.update(address);
        }

        int result = await unitOfWork.saveChanges();

        if (result < 1)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "could not update store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Store? store = await unitOfWork.StoreRepository.getStore(user.Store.Id);
        store.Addresses = await unitOfWork.AddressRepository.getAllAddressByOwnerId(store!.Id);

        return new Result<StoreDto?>
        (
            data: store?.toDto(config.getKey("url_file")),
            message: "error while update store Data",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<StoreDto?>> getStoreByUserId(Guid userId)
    {
        Store? store = await unitOfWork.StoreRepository.getStoreByUserId(userId);

        if (store is null)
            return new Result<StoreDto?>
            (
                data: null,
                message: "store not found",
                isSeccessful: false,
                statusCode: 404
            );

        return new Result<StoreDto?>
        (
            data: store.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<StoreDto?>> getStoreByStoreId(Guid id)
    {
        Store? store = await unitOfWork.StoreRepository.getStore(id);

        if (store is null)
            return new Result<StoreDto?>
            (
                data: null,
                message: "store not found",
                isSeccessful: false,
                statusCode: 404
            );

        return new Result<StoreDto?>
        (
            data: store.toDto(config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<StoreDto>?>> getStores(Guid adminId, int pageNumber, int pageSize)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var isValide = user.isValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<List<StoreDto>?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        List<StoreDto> stores = (await unitOfWork.StoreRepository
                .getStores(pageNumber, pageSize)
            ).Select(st => st.toDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<StoreDto>?>
        (
            data: stores,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool?>> updateStoreStatus(Guid adminId, Guid storeId)
    {
        User? user = await unitOfWork.UserRepository
            .getUser(adminId);

        var isValide = user.isValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<bool?>(
                isSeccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Store? store = await unitOfWork.StoreRepository.getStore(storeId);

        if (store is null)
            return new Result<bool?>
            (
                data: null,
                message: "store not found",
                isSeccessful: false,
                statusCode: 404
            );

        isValide = store.user.isValidateFunc(true);

        if (isValide is null && store.UserId != user!.Id)
        {
            return new Result<bool?>(
                isSeccessful: false,
                data: null,
                message: "only Admin can update his store Status",
                statusCode: 404
            );
        }


        store.IsBlock = !store.IsBlock;
        
        unitOfWork.StoreRepository.update(store);
        int result = await unitOfWork.saveChanges();
        if (result == 0)
            return new Result<bool?>
            (
                data: null,
                message: "error while update store status",
                isSeccessful: false,
                statusCode: 400
            );
        
        await hubContext.Clients.All.SendAsync("storeStatus", new StoreStatusDto
        {
            StoreId = storeId,
            Status = true
        });
        return new Result<bool?>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
}