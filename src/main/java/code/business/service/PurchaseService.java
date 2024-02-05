package code.business.service;

import code.business.dao.CustomerDAO;
import code.business.dao.ProductDAO;
import code.business.dao.PurchaseDAO;
import code.domain.Customer;
import code.domain.Product;
import code.domain.Purchase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class PurchaseService {

   private final PurchaseDAO purchaseDAO;
   private final CustomerDAO customerDAO;
   private final ProductDAO productDAO;

   Integer buyProduct(Integer customerId, Integer productId, int quantity) {
      Customer customer = customerDAO.getById(customerId).orElseThrow();
      Product product = productDAO.getById(productId).orElseThrow();

      Purchase newPurchase = Purchase.builder()
              .customer(customer)
              .product(product)
              .quantity(quantity)
              .timeOfPurchase(OffsetDateTime.now())
              .build();
      return purchaseDAO.add(newPurchase);
   }
}