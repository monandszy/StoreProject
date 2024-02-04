package code.infrastructure.database.repository;

import code.business.dao.ProductDAO;
import code.domain.Producer;
import code.domain.Product;
import code.domain.exception.LoadedObjectIsModifiedException;
import code.domain.exception.ObjectIdNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements ProductDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Product> loadedProducts = new TreeMap<>();
   private final ProducerRepository producerRepository;

   @Override
   public Integer add(Product product) {
      if (Objects.nonNull(product.getId()))
         throw new ObjectIdNotAllowedException();
      Producer producer = product.getProducer();
      Integer producerId = producerRepository.add(producer);
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("product");
      simpleJdbcInsert.setGeneratedKeyName("id");
      simpleJdbcInsert.setSchemaName("zajavka_store");
      Map<String, Object> params = Map.of(
              "code", product.getCode(),
              "name", product.getName(),
              "price", product.getPrice(),
              "adults_only", product.isAdultsOnly(),
              "description", product.getDescription(),
              "producer_id", producerId
      );
      return (Integer) simpleJdbcInsert.executeAndReturnKey(params);
   }

   @Override
   public Optional<Product> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      RowMapper<Product> productRowMapper = (rs, rowNum) -> Product.builder()
              .id(rs.getInt("id"))
              .name(rs.getString("name"))
              .code(rs.getString("code"))
              .price(rs.getBigDecimal("price"))
              .adultsOnly(rs.getBoolean("adults_only"))
              .description(rs.getString("description"))
              .producer(producerRepository.get(rs.getInt("producer_id")).orElseThrow())
              .build();

      String sql = "SELECT * FROM zajavka_store.product WHERE id = ?";
      List<Product> result = jdbcTemplate.query(sql, productRowMapper, id);

      Optional<Product> any = result.stream().findAny();
      if (any.isPresent()) {
         Product loadedProduct = any.get();
         Integer loadedId = loadedProduct.getId();
         if (loadedProducts.containsKey(loadedId)) {
            Product existingProduct = loadedProducts.get(loadedId);
            if (existingProduct.equals(loadedProduct)) {
               return Optional.of(existingProduct);
            } else {
               throw new LoadedObjectIsModifiedException();
            }
         } else {
            loadedProducts.put(loadedId, loadedProduct);
         }
      }
      return any;
   }

   @Override
   public Product update(Integer productId, String[] params) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "UPDATE zajavka_store.product SET code = ?, name = ?, price = ?," +
              " adults_only = ?, description = ?, producer_id = ? WHERE id = ?";
      jdbcTemplate.update(sql,
              params[0], params[1], new BigDecimal(params[2]),
              Boolean.valueOf(params[3]), params[4], Integer.valueOf(params[5]), productId);
      loadedProducts.remove(productId);
      return get(productId).orElseThrow();
   }

   @Override
   public void delete(Integer productId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.product WHERE id = ?";
      jdbcTemplate.update(sql, productId);
      loadedProducts.remove(productId);
   }
}