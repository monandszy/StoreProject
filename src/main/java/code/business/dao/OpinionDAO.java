package code.business.dao;

import code.domain.Opinion;

import java.util.List;

public interface OpinionDAO extends DAO<Opinion> {

   void deleteWherePropertyIn(Object property, List<Integer> whereAgeBelowIds);

   void deleteWherePropertyNotIn(Object property, List<Integer> whereLowStars);

   List<Opinion> getWhereLowStars();

   List<Opinion> getValidOpinions();
}