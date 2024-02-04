package code.infrastructure.database.repository;

import code.business.dao.OpinionDAO;
import code.domain.Opinion;
import code.domain.exception.LoadedObjectIsModifiedException;
import code.domain.exception.ObjectIdNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class OpinionRepository implements OpinionDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Opinion> loadedOpinions = new TreeMap<>();
   private final ProductRepository productRepository;
   private final CustomerRepository customerRepository;
   public static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

   @Override
   public Integer add(Opinion opinion) {
      if (Objects.nonNull(opinion.getId()))
         throw new ObjectIdNotAllowedException();
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("opinion");
      simpleJdbcInsert.setGeneratedKeyName("id");
      simpleJdbcInsert.setSchemaName("zajavka_store");
      Integer customerId = customerRepository.add(opinion.getCustomer());
      Integer productId = productRepository.add(opinion.getProduct());
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customer_id", customerId)
              .addValue("product_id", productId)
              .addValue("stars", opinion.getStars())
              .addValue("time_of_comment", DATABASE_DATE_FORMAT.format(opinion.getTimeOfComment()));
      return (Integer) simpleJdbcInsert.executeAndReturnKey(params);

   }

   @Override
   public Optional<Opinion> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      RowMapper<Opinion> opinionRowMapper =(rs, rowNum) ->  Opinion.builder()
              .id(rs.getInt("id"))
              .product(productRepository.get(rs.getInt("product_id")).orElseThrow())
              .customer(customerRepository.get(rs.getInt("customer_id")).orElseThrow())
              .stars(rs.getInt("stars"))
              .timeOfComment(OffsetDateTime.parse(rs.getString("time_of_comment"), DATABASE_DATE_FORMAT))
              .build();
      String sql = "SELECT * FROM zajavka_store.producer WHERE id = ?";
      List<Opinion> result = jdbcTemplate.query(sql, opinionRowMapper, id);

      Optional<Opinion> any = result.stream().findAny();
      if (any.isPresent()) {
         Opinion loadedOpinion = any.get();
         Integer loadedId = loadedOpinion.getId();
         if (loadedOpinions.containsKey(loadedId)) {
            Opinion existingOpinion = loadedOpinions.get(loadedId);
            if (existingOpinion.equals(loadedOpinion)) {
               return Optional.of(existingOpinion);
            } else {
               throw new LoadedObjectIsModifiedException();
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
      String sql = "UPDATE zajavka_store.opinion SET customer_id = :customerId, product_id = :productId, " +
              "stars = :stars, time_of_comment = :timeOfComment WHERE id = :id";
      MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
              .addValue("customerId", Integer.valueOf(params[0]))
              .addValue("productId", Integer.valueOf(params[1]))
              .addValue("stars", Integer.valueOf(params[2]))
              .addValue("timeOfComment", DATABASE_DATE_FORMAT.format(OffsetDateTime.parse(params[3])))
              .addValue("id", opinionId);
      namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
      loadedOpinions.remove(opinionId);
      return get(opinionId).orElseThrow();
   }

   @Override
   public void delete(Integer opinionId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.opinion WHERE id = ?";
      jdbcTemplate.update(sql, opinionId);
      loadedOpinions.remove(opinionId);
   }
}