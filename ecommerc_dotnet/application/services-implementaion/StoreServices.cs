using ecommerc_dotnet.application.Repository;
using ecommerc_dotnet.core.interfaces.Repository;
using ecommerc_dotnet.core.interfaces.services;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
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

    public async Task<Result<StoreDto?>> createStore(
        CreateStoreDto store,
        Guid userId)
    {
        User? user = await _userRepository
            .getUser(userId);
        if (user is null)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not allowed to create store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        bool isExist = await _storeRepository.isExist(store.Name);

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
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while saving store images",
                isSeccessful: false,
                statusCode: 400
            );
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

        int result = 0;
        result = await _storeRepository.addAsync(storeData);

        if (result == 0)
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSeccessful: false,
                statusCode: 400
            );
        result = await _addressRepository.addAsync(address);
        if (result == 0)
        {
            await _addressRepository.deleteAsync(id);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        Store? savedStore = await _storeRepository.getStore(id);

        return new Result<StoreDto?>
        (
            data: savedStore?.toDto(_config.getKey("url_file")),
            message: "",
            isSeccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<StoreDto?>> updateStore(
        UpdateStoreDto store,
        Guid userId
    )
    {
        if (store.isEmpty())
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
        if (user is null)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not allowed to create store",
                isSeccessful: false,
                statusCode: 400
            );
        }

        if (store.Name is not null)
        {
            bool isExist = await _storeRepository.isExist(store.Name);

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

        Store? storeData = await _storeRepository.getStoreByUserId(userId);

        if (storeData is null)
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not has store",
                isSeccessful: false,
                statusCode: 404
            );

        string? wallperper = null, smallImage = null;

        if (store.WallpaperImage is not null)
            wallperper = await clsUtil.saveFile(
                store.WallpaperImage,
                EnImageType.STORE,
                _host);
        if (store.SmallImage is not null)
            smallImage = await clsUtil.saveFile(
                store.SmallImage,
                EnImageType.STORE,
                _host);
        storeData.SmallImage = smallImage ?? storeData.SmallImage;
        storeData.WallpaperImage = wallperper ?? storeData.WallpaperImage;
        storeData.Name = store.Name ?? storeData.Name;
        storeData.UpdatedAt = DateTime.Now;

        int result = 0;
        result = await _storeRepository.updateAsync(storeData);

        if (result == 0)
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while update store Data",
                isSeccessful: false,
                statusCode: 400
            );

        if (store?.Longitude is not null && store?.Latitude is not null)
        {
            Address? address = await _addressRepository
                .getAddressByOwnerId(storeData.Id);

            if (address is null)
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "store not has any address",
                    isSeccessful: false,
                    statusCode: 404
                );
            address.Title = store?.Name ?? address.Title;
            address.UpdatedAt = DateTime.Now;
            address.Longitude = (decimal)store?.Longitude!;
            address.Latitude = (decimal)store!.Latitude;
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

        Store? updatedStore = await _storeRepository.getStoreByUserId(userId);


        return new Result<StoreDto?>
        (
            data: updatedStore?.toDto(_config.getKey("url_file")),
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
        if (user is null)
        {
            return new Result<List<StoreDto>?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<List<StoreDto>?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 400
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

    public async Task<Result<int>> getStoresCount(Guid adminId)
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<int>
            (
                data: 0,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<int>
            (
                data: 0,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 404
            );
        }

        int storesCount = await _storeRepository.getStoresCount();

        return new Result<int>
        (
            data: storesCount,
            message: "",
            isSeccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool?>> updateStoreStatus(Guid adminId, Guid storeId)
    {
        User? user = await _userRepository
            .getUser(adminId);
        if (user is null)
        {
            return new Result<bool?>
            (
                data: null,
                message: "user not found",
                isSeccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<bool?>
            (
                data: null,
                message: "not authorized user",
                isSeccessful: false,
                statusCode: 404
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