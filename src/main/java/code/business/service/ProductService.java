package code.business.service;

import code.business.dao.ProductDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductService {

   private final ProductDAO productDAO;
}