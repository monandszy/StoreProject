package code.infrastructure.database.repository;

import code.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomerRepository implements code.business.dao.CustomerRepository {

  private final SimpleDriverDataSource simpleDriverDataSource;
  private final CRUDRepository<Customer> crudRepository;
  private Map<Integer, Customer> loadedCustomers = new TreeMap<>();

  private final String INSERT_SQL = "INSERT INTO spring_store.customer " +
    "(user_name, email, name, surname, date_of_birth) VALUES (?, ?, ?, ?, ?)";

  @Override
  public Integer add(Customer customer) {
    if (Objects.nonNull(customer.getId()))
      return updateWhereId(customer.getId(), customer.getParams()).getId();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, customer.getUserName());
      ps.setString(2, customer.getEmail());
      ps.setString(3, customer.getName());
      ps.setString(4, customer.getSurname());
      ps.setDate(5, Date.valueOf(customer.getDateOfBirth()));
      return ps;
    }, keyHolder);

    return (Integer) keyHolder.getKeys().get("id");
  }

  private static RowMapper<Customer> getCustomerRowMapper() {
    RowMapper<Customer> customerRowMapper = (rs, rowNum) -> Customer.builder()
      .id(rs.getInt("id"))
      .userName(rs.getString("user_name"))
      .email(rs.getString("email"))
      .name(rs.getString("name"))
      .surname(rs.getString("surname"))
      .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
      .build();
    return customerRowMapper;
  }

  @Override
  public Optional<Customer> getById(Integer id) {
    if (loadedCustomers.containsKey(id)) {
      return Optional.of(loadedCustomers.get(id));
    }
    RowMapper<Customer> customerRowMapper = getCustomerRowMapper();
    Optional<Customer> any = crudRepository.get("customer", "id", "=", id, customerRowMapper).stream().findAny();
    if (any.isPresent()) {
      Customer loadedCustomer = any.get();
      loadedCustomers.put(loadedCustomer.getId(), loadedCustomer);
    }
    return any;
  }

  private final String UPDATE_SQL = "UPDATE spring_store.customer SET " +
    "user_name = :userName, email = :email, name = :name, surname = :surname, date_of_birth = :dateOfBirth" +
    "  WHERE id = :id";

  private static MapSqlParameterSource getParameterToNamedMap(Integer customerId, String[] params) {
    MapSqlParameterSource customerParamsToNamedMap = new MapSqlParameterSource()
      .addValue("userName", params[0])
      .addValue("email", params[1])
      .addValue("name", params[2])
      .addValue("surname", params[3])
      .addValue("dateOfBirth", Date.valueOf(params[4]))
      .addValue("id", customerId);
    return customerParamsToNamedMap;
  }

  @Override
  public Customer updateWhereId(Integer customerId, String[] params) {
    MapSqlParameterSource customerParamsToNamedMap = getParameterToNamedMap(customerId, params);
    crudRepository.updateById(UPDATE_SQL, customerParamsToNamedMap);
    loadedCustomers.remove(customerId);
    return getById(customerId).orElseThrow();
  }

  @Override
  public void deleteById(Integer customerId) {
    crudRepository.delete("customer", "id", customerId);
    loadedCustomers.remove(customerId);
  }

  @Override
  public void deleteAll() {
    crudRepository.delete("customer", 1, 1);
    loadedCustomers.clear();
  }

  @Override
  public List<Customer> getAll() {
    return crudRepository.get("customer", 1, 1, getCustomerRowMapper());
  }

  @Override
  public List<Customer> getWhereAgeBelow(int age) {
    Date ageGate = Date.valueOf(LocalDate.now().minusYears(age));
    return crudRepository.get("customer", "date_of_birth", "<", ageGate, getCustomerRowMapper());
  }

  @Override
  public void deleteWhereIdIn(List<Integer> ids) {
    List<String> preparedList = ids.stream().map(Object::toString).toList();
    crudRepository.deleteInList("customer", "id", preparedList);
    this.loadedCustomers = loadedCustomers.entrySet().stream().filter(e -> ids.contains(e.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (l, r) -> {
        throw new RuntimeException();
      }, TreeMap::new));
  }
}