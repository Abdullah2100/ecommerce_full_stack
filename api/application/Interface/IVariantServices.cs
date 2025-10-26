using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IVariantServices
{
   Task<Result<VariantDto?>> CreateVariant(CreateVariantDto variantDto,Guid adminId); 
   Task<Result<VariantDto?>> UpdateVariant(UpdateVariantDto variantDto,Guid adminId); 
   Task<Result<bool>> DeleteVariant(Guid vairantId,Guid adminId); 
   Task<Result<List<VariantDto>>> GetVariants(int page,int pageSize); 
}