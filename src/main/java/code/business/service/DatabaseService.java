package code.business.service;

import code.business.dao.CustomerRepository;
import code.business.dao.OpinionRepository;
import code.business.dao.ProducerRepository;
import code.business.dao.ProductRepository;
import code.business.dao.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class DatabaseService {

   private final CustomerRepository customerRepository;
   private final ProducerRepository producerRepository;
   private final ProductRepository productRepository;
   private final PurchaseRepository purchaseRepository;
   private final OpinionRepository opinionRepository;

   @Transactional
   public void deleteAll() {
      opinionRepository.deleteAll();
      purchaseRepository.deleteAll();
      productRepository.deleteAll();
      producerRepository.deleteAll();
      customerRepository.deleteAll();
   }
}