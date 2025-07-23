namespace ecommerc_dotnet.di.Repository;

public interface IRepository<T> where T : class
{
    Task<IEnumerable<T>> GetAllAsync(string[]? include = null);
    Task<IEnumerable<T>> GetAllAsync(int page,int length,string[]? include = null);
    Task<T> GetByIdAsync(Guid id,string[]? includes= null);
    Task AddAsync(T entity);
    Task UpdateAsync(T entity);
    Task DeleteAsync(Guid id);
}