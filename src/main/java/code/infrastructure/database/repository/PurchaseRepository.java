package code.infrastructure.database.repository;

import code.business.dao.PurchaseDAO;
import code.domain.Customer;
import code.domain.Producer;
import code.domain.Product;
import code.domain.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class PurchaseRepository implements PurchaseDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Purchase> loadedPurchases = new TreeMap<>();

   @Override
   public Integer add(Purchase purchase) {
      if (Objects.nonNull(purchase.getId()))
         throw new RuntimeException("Adding object with id present might result in duplicates, please use update instead");
      Integer customerId = purchase.getCustomer().getId();
      Integer productId = purchase.getProduct().getId();
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("purchase");
      simpleJdbcInsert.setGeneratedKeyName("id");
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customer_id", customerId)
              .addValue("product_id", productId)
              .addValue("quantity", purchase.getQuantity())
              .addValue("time_of_purchase", purchase.getTimeOfPurchase());
      return (Integer) simpleJdbcInsert.executeAndReturnKey(params);
   }

   @Override
   public Optional<Purchase> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      BeanPropertyRowMapper<Purchase> purchaseBeanPropertyRowMapper
              = BeanPropertyRowMapper.newInstance(Purchase.class);
      String sql = "SELECT FROM purchase WHERE id = ?";
      List<Purchase> result = jdbcTemplate.query(sql, purchaseBeanPropertyRowMapper, id);

      Optional<Purchase> any = result.stream().findAny();
      if (any.isPresent()) {
         Purchase loadedPurchase = any.get();
         Integer loadedId = loadedPurchase.getId();
         if (loadedPurchases.containsKey(loadedId)) {
            Purchase existingPurchase = loadedPurchases.get(loadedId);
            if (existingPurchase.equals(loadedPurchase)) {
               return Optional.of(existingPurchase);
            } else {
               throw new RuntimeException("This object is already loaded and has been modified, update database before fetching");
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
      String sql = "UPDATE purchase SET customer_id = :customerId, product_Id = :productId, quantity = :quantity, time_of_purchase = :timeOfPurchase WHERE id = :id";
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("customerId", inputParams[0])
              .addValue("productId", inputParams[1])
              .addValue("quantity", inputParams[2])
              .addValue("timeOfPurchase", inputParams[3])
              .addValue("id", purchaseId);
      namedParameterJdbcTemplate.update(sql, params);
      loadedPurchases.remove(purchaseId);
      return get(purchaseId).orElseThrow(() -> new RuntimeException("Error while updating, objectId has changed"));
   }

   @Override
   public void delete(Integer purchaseId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM purchase WHERE id = ?";
      jdbcTemplate.update(sql, purchaseId);
      loadedPurchases.remove(purchaseId);
   }
}