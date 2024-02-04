package code.infrastructure.database.repository;

import code.business.dao.PurchaseDAO;
import code.domain.Purchase;
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
public class PurchaseRepository implements PurchaseDAO {

   public static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Purchase> loadedPurchases = new TreeMap<>();
   private final ProductRepository productRepository;
   private final CustomerRepository customerRepository;

   @Override
   public Integer add(Purchase purchase) {
      if (Objects.nonNull(purchase.getId()))
         throw new ObjectIdNotAllowedException();
      Integer customerId = customerRepository.add(purchase.getCustomer());
      Integer productId = productRepository.add(purchase.getProduct());
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("purchase");
      simpleJdbcInsert.setGeneratedKeyName("id");
      simpleJdbcInsert.setSchemaName("zajavka_store");
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customer_id", customerId)
              .addValue("product_id", productId)
              .addValue("quantity", purchase.getQuantity())
              .addValue("time_of_purchase", DATABASE_DATE_FORMAT.format(purchase.getTimeOfPurchase())); // .withOffsetSameInstant(ZoneOffset.UTC)
      return (Integer) simpleJdbcInsert.executeAndReturnKey(params);
   }

   @Override
   public Optional<Purchase> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      RowMapper<Purchase> purchaseRowMapper = (resultSet, rowNum) -> Purchase.builder()
              .id(resultSet.getInt("id"))
              .product(productRepository.get(resultSet.getInt("product_id")).orElseThrow())
              .customer(customerRepository.get(resultSet.getInt("customer_id")).orElseThrow())
              .quantity(resultSet.getInt("quantity"))
              .timeOfPurchase(OffsetDateTime.parse(resultSet.getString("time_of_purchase"), DATABASE_DATE_FORMAT))
              .build();
      String sql = "SELECT * FROM zajavka_store.purchase WHERE id = ?";
      List<Purchase> result = jdbcTemplate.query(sql, purchaseRowMapper, id);

      Optional<Purchase> any = result.stream().findAny();
      if (any.isPresent()) {
         Purchase loadedPurchase = any.get();
         Integer loadedId = loadedPurchase.getId();
         if (loadedPurchases.containsKey(loadedId)) {
            Purchase existingPurchase = loadedPurchases.get(loadedId);
            if (existingPurchase.equals(loadedPurchase)) {
               return Optional.of(existingPurchase);
            } else {
               throw new LoadedObjectIsModifiedException();
            }
         } else {
            loadedPurchases.put(loadedId, loadedPurchase);
         }
      }
      return any;
   }

   @Override
   public Purchase update(Integer purchaseId, String[] inputParams) {
      NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(simpleDriverDataSource);
      String sql = "UPDATE zajavka_store.purchase SET customer_id = :customerId, product_id = :productId," +
              " quantity = :quantity, time_of_purchase = :timeOfPurchase::timestamp WHERE id = :id";
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customerId", Integer.valueOf(inputParams[0]))
              .addValue("productId", Integer.valueOf(inputParams[1]))
              .addValue("quantity", Integer.valueOf(inputParams[2]))
              .addValue("timeOfPurchase", DATABASE_DATE_FORMAT.format(OffsetDateTime.parse(inputParams[3])))
              .addValue("id", purchaseId);
      namedParameterJdbcTemplate.update(sql, params);
      loadedPurchases.remove(purchaseId);
      return get(purchaseId).orElseThrow();
   }

   @Override
   public void delete(Integer purchaseId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.purchase WHERE id = ?";
      jdbcTemplate.update(sql, purchaseId);
      loadedPurchases.remove(purchaseId);
   }
}