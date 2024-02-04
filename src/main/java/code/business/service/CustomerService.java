package code.business.service;

import code.business.dao.CustomerDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

   private final CustomerDAO customerDAO;
}