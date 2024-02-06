package code.business.dao;

import code.domain.Customer;

import java.util.List;

public interface CustomerRepository extends DAO<Customer> {

   List<Customer> getWhereAgeBelow(int age);

   void deleteWhereIdIn(List<Integer> ids);
}