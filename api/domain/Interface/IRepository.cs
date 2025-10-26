namespace api.domain.Interface;

public interface IRepository<T> where T : class
{
    void  Add(T entity); 
    void Update(T entity);
   
}