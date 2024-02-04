package code.infrastructure.database.repository;

import code.business.dao.CustomerDAO;
import code.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class CustomerRepository implements CustomerDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Customer> loadedCustomers = new TreeMap<>();

   @Override
   public Integer add(Customer customer) {
      if (Objects.nonNull(customer.getId()))
         throw new RuntimeException("Adding object with id present might result in duplicates, please use update instead");
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "INSERT INTO zajavka_store.customer (user_name, email, name, surname, date_of_birth) VALUES (?, ?, ?, ?, ?)";

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(connection -> {
         PreparedStatement ps = connection
                 .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
         ps.setString(1, customer.getUserName());
         ps.setString(2, customer.getEmail());
         ps.setString(3, customer.getName());
         ps.setString(4, customer.getSurname());
         ps.setDate(5, Date.valueOf(customer.getDateOfBirth()));
         return ps;
      }, keyHolder);

      return (Integer) keyHolder.getKeys().get("id");
   }


   @Override
   public Optional<Customer> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "SELECT * FROM zajavka_store.customer WHERE id = ?";
      RowMapper<Customer> customerRowMapper = (resultSet, rowNum) -> Customer.builder()
              .id(resultSet.getInt("id"))
              .userName(resultSet.getString("user_name"))
              .email(resultSet.getString("email"))
              .name(resultSet.getString("name"))
              .surname(resultSet.getString("surname"))
              .dateOfBirth(resultSet.getDate("date_of_birth").toLocalDate())
              .build();
      List<Customer> result = jdbcTemplate.query(sql, customerRowMapper, id);

      Optional<Customer> any = result.stream().findAny();
      if (any.isPresent()) {
         Customer loadedCustomer = any.get();
         Integer loadedId = loadedCustomer.getId();
         if (loadedCustomers.containsKey(loadedId)) {
            Customer existingCustomer = loadedCustomers.get(loadedId);
            if (existingCustomer.equals(loadedCustomer)) {
               return Optional.of(existingCustomer);
            } else {
               throw new RuntimeException("This object is already loaded and has been modified, update database before fetching");
            }
         } else {
            loadedCustomers.put(loadedId, loadedCustomer);
         }
      }
      return any;
   }

   @Override
   public Customer update(Integer customerId, String[] params) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "UPDATE zajavka_store.customer SET " +
              "user_name = ?, email = ?, name = ?, surname = ?, date_of_birth = ?" +
              "  WHERE id = ?";
      jdbcTemplate.update(sql, params[0], params[1], params[2],
              params[3], Date.valueOf(params[4]), customerId);
      loadedCustomers.remove(customerId);
      return get(customerId).orElseThrow(() -> new RuntimeException("Error while updating, objectId has changed"));
   }

   @Override
   public void delete(Integer customerId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.customer WHERE id = ?";
      jdbcTemplate.update(sql, customerId);
      loadedCustomers.remove(customerId);
   }
}