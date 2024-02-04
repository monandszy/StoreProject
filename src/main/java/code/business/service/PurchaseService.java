package code.business.service;

import code.business.dao.PurchaseDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PurchaseService {

   private final PurchaseDAO purchaseDAO;
}