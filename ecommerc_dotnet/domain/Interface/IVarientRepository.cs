using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.Interface;

namespace ecommerc_dotnet.application.Repository;

public interface IVarientRepository:IRepository<Varient>
{
 Task<Varient?> getVarient(Guid id);
 Task<List<Varient>> getVarients(int page,int lenght);
 Task<int> getVarientCount();

 Task<bool> isExist(Guid id);
 Task<bool> isExist(string name);
 Task<bool> isExist(string name,Guid id);
 void deleteAsync(Guid id);
}