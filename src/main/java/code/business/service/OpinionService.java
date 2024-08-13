package code.business.service;

import code.business.dao.OpinionRepository;
import code.domain.Opinion;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OpinionService {

  private final OpinionRepository opinionRepository;

  @Transactional
  void validateThatOpinionMatchesPurchase() {
    List<Integer> whereValidOpinions = opinionRepository.getValidOpinions().stream().map(Opinion::getId).toList();
    opinionRepository.deleteWherePropertyNotIn("id", whereValidOpinions);
  }

  @Transactional
  void adjustQuestionableOpinions() {
    List<Integer> whereLowStars = opinionRepository.getWhereLowStars().stream().map(Opinion::getId).toList();
    opinionRepository.deleteWherePropertyIn("id", whereLowStars);
  }
}