package code.business.dao;

import java.util.Optional;

public interface DAO<T> {

   Integer add(T t);

   Optional<T> get(Integer id);

   T update(Integer id, String[] params);

   void delete(Integer id);
}