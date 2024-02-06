package code.business.service;

import code.business.dao.OpinionRepository;
import code.business.dao.ProductRepository;
import code.business.dao.PurchaseRepository;
import code.domain.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

   private final ProductRepository productRepository;
   private final PurchaseRepository purchaseRepository;
   private final OpinionRepository opinionRepository;

   @Transactional
   void deleteQuestionableProducts() {
      List<Integer> list = productRepository.getQuestionableProducts().stream().map(Product::getId).toList();
      opinionRepository.deleteWherePropertyIn("product_id", list);
      purchaseRepository.deleteWherePropertyIn("product_id", list);
      productRepository.deleteWhereIdIn(list);
   }
}