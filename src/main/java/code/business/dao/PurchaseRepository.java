package code.business.dao;

import code.domain.Purchase;

import java.util.List;

public interface PurchaseRepository extends DAO<Purchase> {
   void deleteWherePropertyIn(Object property, List<Integer> whereAgeBelowIds);
}