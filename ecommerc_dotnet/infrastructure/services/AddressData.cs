using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class AddressData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;

    public AddressData(IUnitOfWork unitOfWork, AppDbContext dbContext)
    {
        _unitOfWork = unitOfWork;
        _dbContext = dbContext;
    }


    public async Task<List<AddressDto>?> getAddressByUserId(Guid userId)
    {
        return await _dbContext
            .Address
            .Where(ad => ad.OwnerId == userId)
            .AsNoTracking()
            .Select(ad=>ad.toDto())
            .ToListAsync();
    }
    
    public async Task<Address?> getAddressById(Guid id)
    {
        return await _dbContext
            .Address
            .AsNoTracking()
            .FirstOrDefaultAsync(ad => ad.Id == id);
    }
    

    public async Task<AddressDto?> addUserAddress(
        decimal longitude,
        decimal latitude,
        string title,
        Guid userId
    )
    {
        await _dbContext
            .Address
            .ExecuteUpdateAsync(ad => ad.SetProperty(state => state.IsCurrent, false));

        Address address = new Address
        {
            Longitude = longitude,
            Latitude = latitude,
            Title = title,
            IsCurrent = true,
            Id = clsUtil.generateGuid(),
            CreatedAt = DateTime.Now,
            OwnerId = userId
        };

        await _unitOfWork.AddressRepository.addAsync(address);

        int result = await _unitOfWork.Complate();
        
        return  (result == 0)? null : address?.toDto()??null;
    }


    public async Task<bool?> updateCurrentAddress(
        Guid id,
        Guid userId
    )
    {
        await _dbContext.Address
            .Where(ad => ad.OwnerId == userId && ad.Id != id)
            .ExecuteUpdateAsync(ad => 
                ad.SetProperty(va => va.IsCurrent, false));

        Address? currentAddress = await _dbContext
            .Address
            .FindAsync(id);

        if (currentAddress is  null) return null;
        
        currentAddress.IsCurrent = true;

       int result =   await _dbContext.SaveChangesAsync();
       
       return (result==0)? null:false;

    }

    public async Task<AddressDto?> updateAddress(
        Guid id,
        string? titile = null,
        decimal? longitude = null,
        decimal? latitude = null
    )
    {
            Address? currentAddress = await _dbContext.Address.FindAsync( id);

            if (currentAddress is null) return null;
            currentAddress.Title = titile ?? currentAddress.Title;
            currentAddress.Longitude = longitude ?? currentAddress.Longitude;
            currentAddress.Latitude = latitude ?? currentAddress.Latitude;

            int result = await _dbContext.SaveChangesAsync();
            return (result==0)? null:currentAddress.toDto();
            
    }


    public async Task<bool> deleteAddress(
        Guid id
    )
    {
           await _unitOfWork.AddressRepository.deleteAsync(id);
           int result = await _unitOfWork.Complate();
           return (result != 0);
    }

    public async Task<int> getAddressCountForUser(
        Guid userId
    )
    {
            return await _dbContext.Address
                .AsNoTracking()
                .CountAsync(u => u.OwnerId == userId && u.Id != userId);
    }

    
}