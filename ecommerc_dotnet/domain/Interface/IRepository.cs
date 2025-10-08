namespace ecommerc_dotnet.application.Repository;

public interface IRepository<T> where T : class
{
    void  add(T entity); 
    void update(T entity);
   
}