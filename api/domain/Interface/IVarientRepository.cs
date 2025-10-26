using api.domain.entity;

namespace api.domain.Interface;

public interface IVarientRepository:IRepository<Variant>
{
 Task<Variant?> GetVarient(Guid id);
 Task<List<Variant>> GetVarients(int page,int lenght);
 Task<int> GetVarientCount();
 Task<bool> IsExist(Guid id);
 Task<bool> IsExist(string name);
 Task<bool> IsExist(string name,Guid id);
 void Delete(Guid id);
}