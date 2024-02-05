package code.business.service;

import code.business.dao.CustomerDAO;
import code.business.dao.OpinionDAO;
import code.business.dao.PurchaseDAO;
import code.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {

   private final CustomerDAO customerDAO;
   private final OpinionDAO opinionDAO;
   private final PurchaseDAO purchaseDAO;

   @Transactional
   void deleteCustomersWhereAgeBelow16() {
      List<Integer> whereAgeBelowIds = customerDAO.getWhereAgeBelow(16).stream().map(Customer::getId).toList();
      opinionDAO.deleteWherePropertyIn("customer_id", whereAgeBelowIds);
      purchaseDAO.deleteWherePropertyIn("customer_id", whereAgeBelowIds);
      customerDAO.deleteWhereIdIn(whereAgeBelowIds);
   }
}