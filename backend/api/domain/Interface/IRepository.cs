namespace ecommerc_dotnet.domain.Interface;

public interface IRepository<T> where T : class
{
    void  add(T entity); 
    void update(T entity);
   
}