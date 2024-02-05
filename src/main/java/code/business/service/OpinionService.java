package code.business.service;

import code.business.dao.OpinionDAO;
import code.domain.Opinion;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OpinionService {

   private final OpinionDAO opinionDAO;

   @Transactional
   void validateThatOpinionMatchesPurchase() {
      List<Integer> whereValidOpinions = opinionDAO.getValidOpinions().stream().map(Opinion::getId).toList();
      opinionDAO.deleteWherePropertyNotIn("id", whereValidOpinions);
   }

   @Transactional
   void adjustQuestionableOpinions() {
      List<Integer> whereLowStars = opinionDAO.getWhereLowStars().stream().map(Opinion::getId).toList();
      opinionDAO.deleteWherePropertyIn("id", whereLowStars);
   }
}