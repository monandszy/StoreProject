package code.infrastructure.database.repository;

import code.business.dao.ProductDAO;
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

import java.util.HashMap;
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

   @Override
   public Integer add(Product product) {
      if (Objects.nonNull(product.getId()))
         throw new RuntimeException("Adding object with id present might result in duplicates, please use update instead");
      Integer producerId = product.getProducer().getId();

      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("product");
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
      BeanPropertyRowMapper<Product> purchaseBeanPropertyRowMapper
              = BeanPropertyRowMapper.newInstance(Product.class);
      String sql = "SELECT FROM product WHERE id = ?";
      List<Product> result = jdbcTemplate.query(sql, purchaseBeanPropertyRowMapper, id);

      Optional<Product> any = result.stream().findAny();
      if (any.isPresent()) {
         Product loadedProduct = any.get();
         Integer loadedId = loadedProduct.getId();
         if (loadedProducts.containsKey(loadedId)) {
            Product existingProduct = loadedProducts.get(loadedId);
            if (existingProduct.equals(loadedProduct)) {
               return Optional.of(existingProduct);
            } else {
               throw new RuntimeException("This object is already loaded and has been modified, update database before fetching");
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
      String sql = "UPDATE product SET code = ?, name = ?, price = ?, adults_only = ?, description = ?, producer_id = ? WHERE id = ?";
      jdbcTemplate.update(sql, params[0], params[1], params[2],params[3],params[4],params[5], productId);
      loadedProducts.remove(productId);
      return get(productId).orElseThrow(() -> new RuntimeException("Error while updating, objectId has changed"));
   }

   @Override
   public void delete(Integer productId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM product WHERE id = ?";
      jdbcTemplate.update(sql, productId);
      loadedProducts.remove(productId);
   }
}