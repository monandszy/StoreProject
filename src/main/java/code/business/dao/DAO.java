package code.business.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {

   Integer add(T t);

   Optional<T> getById(Integer id);

   T updateWhereId(Integer id, String[] params);

   void deleteById(Integer id);

   void deleteAll();

   List<T> getAll();
}