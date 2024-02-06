package code.business.service;

import code.business.dao.CustomerRepository;
import code.business.dao.OpinionRepository;
import code.business.dao.PurchaseRepository;
import code.domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {

   private final CustomerRepository customerRepository;
   private final OpinionRepository opinionRepository;
   private final PurchaseRepository purchaseRepository;

   @Transactional
   void deleteCustomersWhereAgeBelow16() {
      List<Integer> whereAgeBelowIds = customerRepository.getWhereAgeBelow(16).stream().map(Customer::getId).toList();
      opinionRepository.deleteWherePropertyIn("customer_id", whereAgeBelowIds);
      purchaseRepository.deleteWherePropertyIn("customer_id", whereAgeBelowIds);
      customerRepository.deleteWhereIdIn(whereAgeBelowIds);
   }
}