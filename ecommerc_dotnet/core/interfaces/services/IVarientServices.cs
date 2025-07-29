using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.core.Result;
using ecommerc_dotnet.dto;

namespace ecommerc_dotnet.core.interfaces.services;

public interface IVarientServices
{
   Task<Result<VarientDto?>> createVarient(CreateVarientDto varientDto,Guid adminId); 
   Task<Result<VarientDto?>> updateVarient(UpdateVarientDto varientDto,Guid adminId); 
   Task<Result<bool>> deleteVarient(Guid vairentId,Guid adminId); 
   Task<Result<List<VarientDto>>> getVarients(int page,int pageSize); 
}