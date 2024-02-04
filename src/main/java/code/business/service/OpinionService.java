package code.business.service;

import code.business.dao.OpinionDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OpinionService {

   private final OpinionDAO opinionDAO;
}