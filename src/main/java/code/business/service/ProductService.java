package code.business.service;

import code.business.dao.OpinionDAO;
import code.business.dao.ProductDAO;
import code.business.dao.PurchaseDAO;
import code.domain.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

   private final ProductDAO productDAO;
   private final PurchaseDAO purchaseDAO;
   private final OpinionDAO opinionDAO;

   @Transactional
   void deleteQuestionableProducts() {
      List<Integer> list = productDAO.getQuestionableProducts().stream().map(Product::getId).toList();
      opinionDAO.deleteWherePropertyIn("product_id", list);
      purchaseDAO.deleteWherePropertyIn("product_id", list);
      productDAO.deleteWhereIdIn(list);
   }
}