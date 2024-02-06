package code.business.service;

import code.business.dao.CustomerRepository;
import code.business.dao.ProductRepository;
import code.business.dao.PurchaseRepository;
import code.domain.Customer;
import code.domain.Product;
import code.domain.Purchase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class PurchaseService {

   private final PurchaseRepository purchaseRepository;
   private final CustomerRepository customerRepository;
   private final ProductRepository productRepository;

   Integer buyProduct(Integer customerId, Integer productId, int quantity) {
      Customer customer = customerRepository.getById(customerId).orElseThrow();
      Product product = productRepository.getById(productId).orElseThrow();

      Purchase newPurchase = Purchase.builder()
              .customer(customer)
              .product(product)
              .quantity(quantity)
              .timeOfPurchase(OffsetDateTime.now())
              .build();
      return purchaseRepository.add(newPurchase);
   }
}