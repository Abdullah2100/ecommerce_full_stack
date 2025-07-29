using ecommerc_dotnet.core.entity;
using ecommerc_dotnet.domain.interfaces;
using ecommerc_dotnet.module;

namespace ecommerc_dotnet.infrastructure.repositories;

public interface IVarientRepository:IRepository<Varient>
{
 Task<Varient?> getVarient(Guid id);
 Task<int> getVarientCount();

 Task<bool> isExist(Guid id);
 Task<bool> isExist(string name);
}