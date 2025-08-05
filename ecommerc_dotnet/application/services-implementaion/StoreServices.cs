using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.application.services;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.shared.extentions;
using hotel_api.util;

namespace ecommerc_dotnet.infrastructure.services;

public class StoreServices : IStoreServices
{
    private readonly IStoreRepository _storeRepository;
    private readonly IUserRepository _userRepository;
    private readonly IAddressRepository _addressRepository;
    private readonly IWebHostEnvironment _host;
    private readonly IConfig _config;


    public StoreServices(
        IWebHostEnvironment host,
        IConfig config,
        IStoreRepository storeRepository,
        IUserRepository userRepository,
        IAddressRepository addressRepository
    )
    {
        _host = host;
        _config = config;
        _storeRepository = storeRepository;
        _userRepository = userRepository;
        _addressRepository = addressRepository;
    }

    private void deleteStoreImage(string? wallperper, string? smallImage)
    {
        if (wallperper is not null)
            clsUtil.deleteFile(wallperper, _host);
        if (smallImage is not null)
            clsUtil.deleteFile(smallImage, _host);
    }

    public async Task<Result<StoreDto?>> createStore(
        CreateStoreDto store,
        Guid userId)
    {
        User? user = await _userRepository
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


        if (await _storeRepository.isExist(store.Name))
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

        smallImage = await clsUtil.saveFile(
            store.SmallImage,
            EnImageType.STORE,
            _host);
        wallperper = await clsUtil.saveFile(
            store.WallpaperImage,
            EnImageType.STORE,
            _host);


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
        Store storeData = new Store
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

        int result = await _storeRepository.addAsync(storeData);

        if (result == 0)
        {
            deleteStoreImage(wallperper, smallImage);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        result = await _addressRepository.addAsync(address);
        if (result == 0)
        {
            deleteStoreImage(wallperper, smallImage);
            await _addressRepository.deleteAsync(id);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        storeData.user = user!;
        storeData.Addresses = new List<Address> { address };


        return new Result<StoreDto?>
        (
            data: storeData?.toDto(_config.getKey("url_file")),
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

        User? user = await _userRepository
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
            bool isExist = await _storeRepository.isExist(storeDto.Name, user!.Store!.Id);

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
            wallperper = await clsUtil.saveFile(
                storeDto.WallpaperImage,
                EnImageType.STORE,
                _host);

            deleteStoreImage(user!.Store!.WallpaperImage, null);
        }

        if (storeDto.SmallImage is not null)
        {
            smallImage = await clsUtil.saveFile(
                storeDto.SmallImage,
                EnImageType.STORE,
                _host);
            deleteStoreImage(null, user!.Store?.SmallImage);
        }

        user!.Store!.SmallImage = smallImage ?? user!.Store!.SmallImage;
        user!.Store!.WallpaperImage = wallperper ?? user!.Store!.WallpaperImage;
        user!.Store!.Name = storeDto.Name ?? user!.Store!.Name;
        user!.Store!.UpdatedAt = DateTime.Now;

        int result = await _storeRepository.updateAsync(user!.Store!);

        if (result == 0)
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while update store Data",
                isSeccessful: false,
                statusCode: 400
            );

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
            Address? address = await _addressRepository
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
            result = await _addressRepository.updateAsync(address);

            if (result == 0)
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "error while update store address",
                    isSeccessful: false,
                    statusCode: 400
                );
        }

        Store? store = await _storeRepository.getStore(user.Store.Id);

        return new Result<StoreDto?>
        (
            data: store?.toDto(_config.getKey("url_file")),
            message: "error while update store Data",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<StoreDto?>> getStoreByUserId(Guid userId)
    {
        Store? store = await _storeRepository.getStoreByUserId(userId);

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
            data: store.toDto(_config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<StoreDto?>> getStoreByStoreId(Guid id)
    {
        Store? store = await _storeRepository.getStore(id);

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
            data: store.toDto(_config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<StoreDto>?>> getStores(Guid adminId, int pageNumber, int pageSize)
    {
        User? user = await _userRepository
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

        List<StoreDto> stores = (await _storeRepository
                .getAllAsync(pageNumber, pageSize)
            ).Select(st => st.toDto(_config.getKey("url_file")))
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
        User? user = await _userRepository
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

        Store? store = await _storeRepository.getStore(storeId);

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
        int result = await _storeRepository.updateAsync(store);
        if (result == 0)
            return new Result<bool?>
            (
                data: null,
                message: "error while update store status",
                isSeccessful: false,
                statusCode: 400
            );
        return new Result<bool?>
        (
            data: true,
            message: "",
            isSeccessful: true,
            statusCode: 204
        );
    }
}