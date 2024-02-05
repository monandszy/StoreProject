package code.infrastructure.database.repository;

import code.business.dao.ProductDAO;
import code.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements ProductDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final CRUDRepository<Product> crudRepository;
   private Map<Integer, Product> loadedProducts = new TreeMap<>();
   private final ProducerRepository producerRepository;

   private MapSqlParameterSource getObjectToTableMap(Product product) {
      Integer producerId = producerRepository.add(product.getProducer());
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("code", product.getCode())
              .addValue("name", product.getName())
              .addValue("price", product.getPrice())
              .addValue("adults_only", product.isAdultsOnly())
              .addValue("description", product.getDescription())
              .addValue("producer_id", producerId);
      return params;
   }

   @Override
   public Integer add(Product product) {
      if (Objects.nonNull(product.getId()))
         return updateWhereId(product.getId(), product.getParams()).getId();
      MapSqlParameterSource params = getObjectToTableMap(product);
      return crudRepository.add("product", params);
   }

   private RowMapper<Product> getProductRowMapper() {
      RowMapper<Product> productRowMapper = (rs, rowNum) -> Product.builder()
              .id(rs.getInt("id"))
              .name(rs.getString("name"))
              .code(rs.getString("code"))
              .price(rs.getBigDecimal("price"))
              .adultsOnly(rs.getBoolean("adults_only"))
              .description(rs.getString("description"))
              .producer(producerRepository.getById(rs.getInt("producer_id")).orElseThrow())
              .build();
      return productRowMapper;
   }

   @Override
   public Optional<Product> getById(Integer id) {
      if (loadedProducts.containsKey(id)) {
         return Optional.of(loadedProducts.get(id));
      }
      RowMapper<Product> productRowMapper = getProductRowMapper();
      Optional<Product> any = crudRepository.get("product", "id", id, productRowMapper).stream().findAny();
      if (any.isPresent()) {
         Product loadedProduct = any.get();
         loadedProducts.put(loadedProduct.getId(), loadedProduct);
      }
      return any;
   }

   String UPDATE_SQL = "UPDATE zajavka_store.product SET code = :code, name = :name, price = :price," +
           " adults_only = :adultsOnly, description = :description, producer_id = :producerId WHERE id = :id";

   private static MapSqlParameterSource getParameterToNamedMapper(Integer productId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
              .addValue("code", params[0])
              .addValue("name", params[1])
              .addValue("price", new BigDecimal(params[2]))
              .addValue("adultsOnly", Boolean.valueOf(params[3]))
              .addValue("description", params[4])
              .addValue("producerId", Integer.valueOf(params[5]))
              .addValue("id", productId);
      return mapSqlParameterSource;
   }

   @Override
   public Product updateWhereId(Integer productId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = getParameterToNamedMapper(productId, params);
      crudRepository.updateById(UPDATE_SQL, mapSqlParameterSource);
      loadedProducts.remove(productId);
      return getById(productId).orElseThrow();
   }

   @Override
   public void deleteById(Integer productId) {
      crudRepository.delete("product", "id", productId);
      loadedProducts.remove(productId);
   }

   @Override
   public void deleteAll() {
      crudRepository.delete("product", 1, 1);
      loadedProducts.clear();
   }

   @Override
   public List<Product> getAll() {
      return crudRepository.get("product", 1, 1, getProductRowMapper());
   }

   @Override
   public List<Product> getQuestionableProducts() {
      return crudRepository.get("product", "adults_only", true, getProductRowMapper());
   }

   @Override
   public void deleteWhereIdIn(List<Integer> ids) {
      List<String> preparedList = ids.stream().map(Object::toString).toList();
      crudRepository.deleteInList("product", "id", preparedList);
      this.loadedProducts = loadedProducts.entrySet().stream().filter(e -> ids.contains(e.getKey()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l, r) -> {
                 throw new RuntimeException();
              }, TreeMap::new));
   }
}