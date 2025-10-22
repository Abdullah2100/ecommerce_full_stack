using ecommerc_dotnet.domain.entity;
using ecommerc_dotnet.domain.Interface;

namespace ecommerc_dotnet.application.Repository;

public interface IUserRepository:IRepository<User>
{
      Task<User?> getUser(Guid id);
      Task<User?> getUser(string email);
      Task<int> getUserCount();
      Task<User?> getUserByStoreId(Guid id);
      Task<List<User>> getUsers(int page,int length);
      Task<User?> getUser(string username ,string password);
      
      Task<bool> isExist(Guid id);
      
      Task<bool> isExist(int role);
      Task<bool> isExistByPhone(string phone);
      Task<bool> isExistByEmail(string email);

      void deleteAsync(Guid id);
}