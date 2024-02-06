package code.business.service;

import code.business.dao.ProducerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProducerService {

   private final ProducerRepository producerRepository;
}