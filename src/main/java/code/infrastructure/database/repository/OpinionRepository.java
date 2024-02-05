package code.infrastructure.database.repository;

import code.business.dao.OpinionDAO;
import code.domain.Opinion;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OpinionRepository implements OpinionDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final CRUDRepository<Opinion> crudRepository;

   private Map<Integer, Opinion> loadedOpinions = new TreeMap<>();
   private final ProductRepository productRepository;
   private final PurchaseRepository purchaseRepository;
   private final CustomerRepository customerRepository;
   public static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

   private MapSqlParameterSource getObjectToTableMap(Opinion opinion) {
      Integer customerId = customerRepository.add(opinion.getCustomer());
      Integer productId = productRepository.add(opinion.getProduct());
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customer_id", customerId)
              .addValue("product_id", productId)
              .addValue("stars", opinion.getStars())
              .addValue("comment", opinion.getComment())
              .addValue("time_of_comment", DATABASE_DATE_FORMAT.format(opinion.getTimeOfComment()));
      return params;
   }

   @Override
   public Integer add(Opinion opinion) {
      if (Objects.nonNull(opinion.getId()))
         return updateWhereId(opinion.getId(), opinion.getParams()).getId();
      MapSqlParameterSource params = getObjectToTableMap(opinion);
      return crudRepository.add("opinion", params);
   }

   private RowMapper<Opinion> getOpinionRowMapper() {
      RowMapper<Opinion> opinionRowMapper = (rs, rowNum) -> Opinion.builder()
              .id(rs.getInt("id"))
              .product(productRepository.getById(rs.getInt("product_id")).orElseThrow())
              .customer(customerRepository.getById(rs.getInt("customer_id")).orElseThrow())
              .stars(rs.getInt("stars"))
              .comment(rs.getString("comment"))
              .timeOfComment(OffsetDateTime.parse(rs.getString("time_of_comment"), DATABASE_DATE_FORMAT))
              .build();
      return opinionRowMapper;
   }

   @Override
   public Optional<Opinion> getById(Integer id) {
      if (loadedOpinions.containsKey(id)) {
         return Optional.of(loadedOpinions.get(id));
      }
      RowMapper<Opinion> opinionRowMapper = getOpinionRowMapper();
      Optional<Opinion> any = crudRepository.get("opinion", "id", id, opinionRowMapper).stream().findAny();
      if (any.isPresent()) {
         Opinion loadedOpinion = any.get();
         loadedOpinions.put(loadedOpinion.getId(), loadedOpinion);
      }
      return any;
   }

   String UPDATE_SQL = "UPDATE zajavka_store.opinion SET customer_id = :customerId, product_id = :productId, " +
           "stars = :stars, time_of_comment = :timeOfComment::timestamp WHERE id = :id";

   @Override
   public Opinion updateWhereId(Integer opinionId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = getParameterToNamedMap(opinionId, params);
      crudRepository.updateById(UPDATE_SQL, mapSqlParameterSource);
      loadedOpinions.remove(opinionId);
      return getById(opinionId).orElseThrow();
   }

   private static MapSqlParameterSource getParameterToNamedMap(Integer opinionId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
              .addValue("customerId", Integer.valueOf(params[0]))
              .addValue("productId", Integer.valueOf(params[1]))
              .addValue("stars", Integer.valueOf(params[2]))
              .addValue("comment", params[3])
              .addValue("timeOfComment", DATABASE_DATE_FORMAT.format(OffsetDateTime.parse(params[4])))
              .addValue("id", opinionId);
      return mapSqlParameterSource;
   }

   @Override
   public void deleteById(Integer opinionId) {
      crudRepository.delete("opinion", "id", opinionId);
      loadedOpinions.remove(opinionId);
   }

   @Override
   public void deleteAll() {
      crudRepository.delete("opinion", 1, 1);
      loadedOpinions.clear();
   }

   @Override
   public List<Opinion> getAll() {
      System.out.println("loadedOpinions" + loadedOpinions);
      return crudRepository.get("opinion", 1, 1, getOpinionRowMapper());
   }

   @Override
   public void deleteWherePropertyIn(Object property, List<Integer> wherePropertyIds) {
      List<String> preparedList = wherePropertyIds.stream().map(Object::toString).toList();
      crudRepository.deleteInList("opinion", property, preparedList);
      this.loadedOpinions = loadedOpinions.entrySet().stream().filter(e -> wherePropertyIds.contains(e.getKey()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l, r) -> {
                 throw new RuntimeException();
              }, TreeMap::new));
   }

   @Override
   public void deleteWherePropertyNotIn(Object property, List<Integer> wherePropertyIds) {
      System.out.println("wherePropertyIds:" + wherePropertyIds);
      System.out.println("getValidOpinions():" + getValidOpinions());
      List<String> preparedList = wherePropertyIds.stream().map(Object::toString).toList();
      if (preparedList.isEmpty()) {
         deleteAll();
         return;
      }
      crudRepository.deleteNotInList("opinion", property, preparedList);
      this.loadedOpinions = loadedOpinions.entrySet().stream().filter(e -> !wherePropertyIds.contains(e.getKey()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l, r) -> {
                 throw new RuntimeException();
              }, TreeMap::new));
   }

   @Override
   public List<Opinion> getWhereLowStars() {
      return crudRepository.get("opinion", "stars", "<", 4, getOpinionRowMapper());
   }

   @Override
   public List<Opinion> getValidOpinions() {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "SELECT op.id, op.customer_id, op.product_id, op.stars, op.comment, op.time_of_comment " +
              "FROM zajavka_store.opinion op, zajavka_store.purchase pu " +
              "WHERE op.customer_id = pu.customer_id AND op.product_id = pu.product_id";
      return jdbcTemplate.query(sql, getOpinionRowMapper());
   }
}