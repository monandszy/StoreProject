package code.infrastructure.database.repository;

import code.domain.Purchase;
import lombok.RequiredArgsConstructor;
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
public class PurchaseRepository implements code.business.dao.PurchaseRepository {

  public static final DateTimeFormatter DATABASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

  private final SimpleDriverDataSource simpleDriverDataSource;
  private final CRUDRepository<Purchase> crudRepository;

  private Map<Integer, Purchase> loadedPurchases = new TreeMap<>();
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;

  private MapSqlParameterSource getObjectToTableMap(Purchase purchase) {
    Integer customerId = customerRepository.add(purchase.getCustomer());
    Integer productId = productRepository.add(purchase.getProduct());
    MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("customer_id", customerId)
      .addValue("product_id", productId)
      .addValue("quantity", purchase.getQuantity())
      .addValue("time_of_purchase", DATABASE_DATE_FORMAT.format(purchase.getTimeOfPurchase())); // .withOffsetSameInstant(ZoneOffset.UTC)
    return params;
  }

  @Override
  public Integer add(Purchase purchase) {
    if (Objects.nonNull(purchase.getId()))
      return updateWhereId(purchase.getId(), purchase.getParams()).getId();
    MapSqlParameterSource params = getObjectToTableMap(purchase);
    return crudRepository.add("purchase", params);
  }

  private RowMapper<Purchase> getPurchaseRowMapper() {
    RowMapper<Purchase> purchaseRowMapper = (resultSet, rowNum) -> Purchase.builder()
      .id(resultSet.getInt("id"))
      .product(productRepository.getById(resultSet.getInt("product_id")).orElseThrow())
      .customer(customerRepository.getById(resultSet.getInt("customer_id")).orElseThrow())
      .quantity(resultSet.getInt("quantity"))
      .timeOfPurchase(OffsetDateTime.parse(resultSet.getString("time_of_purchase"), DATABASE_DATE_FORMAT))
      .build();
    return purchaseRowMapper;
  }

  @Override
  public Optional<Purchase> getById(Integer id) {
    if (loadedPurchases.containsKey(id)) {
      return Optional.of(loadedPurchases.get(id));
    }
    RowMapper<Purchase> purchaseRowMapper = getPurchaseRowMapper();
    Optional<Purchase> any = crudRepository.get("purchase", "id", id, purchaseRowMapper).stream().findAny();
    if (any.isPresent()) {
      Purchase loadedPurchase = any.get();
      loadedPurchases.put(loadedPurchase.getId(), loadedPurchase);
    }
    return any;
  }

  String UPDATE_SQL = "UPDATE spring_store.purchase SET customer_id = :customerId, product_id = :productId," +
    " quantity = :quantity, time_of_purchase = :timeOfPurchase::timestamp WHERE id = :id";

  private static MapSqlParameterSource getParameterToNamedMap(Integer purchaseId, String[] inputParams) {
    MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("customerId", Integer.valueOf(inputParams[0]))
      .addValue("productId", Integer.valueOf(inputParams[1]))
      .addValue("quantity", Integer.valueOf(inputParams[2]))
      .addValue("timeOfPurchase", OffsetDateTime.parse(inputParams[3]).format(DATABASE_DATE_FORMAT))
      .addValue("id", purchaseId);
    return params;
  }

  @Override
  public Purchase updateWhereId(Integer purchaseId, String[] inputParams) {
    MapSqlParameterSource params = getParameterToNamedMap(purchaseId, inputParams);
    crudRepository.updateById(UPDATE_SQL, params);
    loadedPurchases.remove(purchaseId);
    return getById(purchaseId).orElseThrow();
  }

  @Override
  public void deleteById(Integer purchaseId) {
    crudRepository.delete("purchase", "id", purchaseId);
    loadedPurchases.remove(purchaseId);
  }

  @Override
  public void deleteAll() {
    crudRepository.delete("purchase", 1, 1);
    loadedPurchases.clear();
  }

  @Override
  public List<Purchase> getAll() {
    return crudRepository.get("purchase", 1, 1, getPurchaseRowMapper());
  }

  @Override
  public void deleteWherePropertyIn(Object property, List<Integer> ids) {
    List<String> preparedList = ids.stream().map(Object::toString).toList();
    crudRepository.deleteInList("purchase", property, preparedList);
    this.loadedPurchases = loadedPurchases.entrySet().stream().filter(e -> ids.contains(e.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l, r) -> {
        throw new RuntimeException();
      }, TreeMap::new));
  }
}