namespace ecommerc_dotnet.application.Repository;

public interface IRepository<T> where T : class
{
    Task<IEnumerable<T>> getAllAsync(int page,int length );
    Task<int> addAsync(T entity);
    Task<int> updateAsync(T entity);
    Task<int> deleteAsync(Guid id);
   
}