package code.business.service;

import code.business.dao.CustomerDAO;
import code.business.dao.OpinionDAO;
import code.business.dao.ProducerDAO;
import code.business.dao.ProductDAO;
import code.business.dao.PurchaseDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class DatabaseService {

   private final CustomerDAO customerDAO;
   private final ProducerDAO producerDAO;
   private final ProductDAO productDAO;
   private final PurchaseDAO purchaseDAO;
   private final OpinionDAO opinionDAO;

   @Transactional
   public void deleteAll() {
      opinionDAO.deleteAll();
      purchaseDAO.deleteAll();
      productDAO.deleteAll();
      producerDAO.deleteAll();
      customerDAO.deleteAll();
   }
}