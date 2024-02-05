package code.business.dao;

import code.domain.Purchase;

import java.util.List;

public interface PurchaseDAO extends DAO<Purchase> {
   void deleteWherePropertyIn(Object property, List<Integer> whereAgeBelowIds);
}