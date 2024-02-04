package code.business.service;

import code.business.dao.ProducerDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProducerService {

   private final ProducerDAO producerDAO;
}