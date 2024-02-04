package code.infrastructure.database.repository;

import code.business.dao.OpinionDAO;
import code.domain.Opinion;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class OpinionRepository implements OpinionDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Opinion> loadedOpinions = new TreeMap<>();

      /*id              SERIAL PRIMARY KEY       NOT NULL,
    customer_id     INT                      NOT NULL,
    product_id      INT                      NOT NULL,
    stars           INTEGER                  NOT NULL,
    comment         TEXT                     NOT NULL,
    time_of_comment TIMESTAMP WITH TIME ZONE NOT NULL,*/

   @Override
   public Integer add(Opinion opinion) {
      return null;

   }

   @Override
   public Optional<Opinion> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      BeanPropertyRowMapper<Opinion> producerBeanPropertyRowMapper
              = BeanPropertyRowMapper.newInstance(Opinion.class);
      String sql = "SELECT FROM producer WHERE id = ?";
      List<Opinion> result = jdbcTemplate.query(sql, producerBeanPropertyRowMapper, id);

      Optional<Opinion> any = result.stream().findAny();
      if (any.isPresent()) {
         Opinion loadedOpinion = any.get();
         Integer loadedId = loadedOpinion.getId();
         if (loadedOpinions.containsKey(loadedId)) {
            Opinion existingOpinion = loadedOpinions.get(loadedId);
            if (existingOpinion.equals(loadedOpinion)) {
               return Optional.of(existingOpinion);
            } else {
               throw new RuntimeException("This object is already loaded and has been modified, update database before fetching");
            }
         } else {
            loadedOpinions.put(loadedId, loadedOpinion);
         }
      }
      return any;
   }
   @Override
   public Opinion update(Integer opinionId, String[] params) {
      NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(simpleDriverDataSource);
      String sql = "UPDATE opinion SET customer_id = :customerId, product_id = :productId, stars = :stars, time_of_comment = :timeOfComment WHERE id = :id";
      MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
              .addValue("customerId", params[0])
              .addValue("productId", params[1])
              .addValue("stars", params[2])
              .addValue("timeOfComment", params[3])
              .addValue("id", opinionId);
      namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
      loadedOpinions.remove(opinionId);
      return get(opinionId).orElseThrow(() -> new RuntimeException("Error while updating, objectId has changed"));
   }

   @Override
   public void delete(Integer opinionId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM opinion WHERE id = ?";
      jdbcTemplate.update(sql, opinionId);
      loadedOpinions.remove(opinionId);
   }
}