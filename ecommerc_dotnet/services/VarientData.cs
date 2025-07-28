using ecommerc_dotnet.context;
using ecommerc_dotnet.dto;
using ecommerc_dotnet.dto.Request;
using ecommerc_dotnet.dto.Response;
using ecommerc_dotnet.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;
using ecommerc_dotnet.module;
using ecommerc_dotnet.UnitOfWork;
using FirebaseAdmin.Auth;
using hotel_api.Services;
using hotel_api.util;
using Microsoft.EntityFrameworkCore;

namespace ecommerc_dotnet.data;

public class VarientData
{
    private readonly AppDbContext _dbContext;
    private readonly IUnitOfWork _unitOfWork;

    public VarientData(
        AppDbContext dbContext,
        IUnitOfWork unitOfWork
        )
    {
        _dbContext = dbContext;
        _unitOfWork = unitOfWork;
    }

    public async Task<VarientDto?> getVarient(Guid id)
    {
        return (await _dbContext.Varients.FindAsync(id))?.toDto();
    }

    public async Task<List<VarientDto>> getVarients(int pageNumber, int pageSize=25)
    {
            return  (await _unitOfWork.VarientRepository.getAllAsync(pageNumber, pageSize))
            .Select(ca => ca.toDto()).ToList();
    }

    public async Task<double> getVarients()
    {
            var result=(await _dbContext
                .Varients
                .AsNoTracking()
                .CountAsync());
            return (double) result / 25;
    }
 
    public async Task<bool> isExist(string name)
    {
           return await _dbContext
                .Varients
                .AsNoTracking()
                .AnyAsync(va => va.Name == name);
    }

    public async Task<bool> isExist(Guid id)
    {
        return await _dbContext.Varients.FindAsync(id) != null;
    }


    public async Task<VarientDto?> createVarient(string name)
    {
            var id = clsUtil.generateGuid();
            Varient varient = new Varient { Id = id, Name = name };
            await _unitOfWork.VarientRepository.addAsync(varient);

           int result =  await _unitOfWork.Complate();

           return (result == 0)?  null : varient?.toDto();
    }

    public async Task<VarientDto?> updateVarient(string? name, Guid id)
    {
        Varient? varient = await _dbContext.Varients.FindAsync(id);
       
        if (varient == null) return null;
        
        varient.Name = name  ?? varient.Name;
        
        var result = await _unitOfWork.Complate();
        return result == 0 ? null : varient.toDto();
    }

    public async Task<bool> deleteVarient(
        Guid varientId
        )
    {
        await _unitOfWork.VarientRepository.deleteAsync(varientId);
        var result = await _unitOfWork.Complate();
        return result != 0;
    }
}