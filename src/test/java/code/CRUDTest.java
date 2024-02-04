package code;

import code.business.dao.CustomerDAO;
import code.business.dao.OpinionDAO;
import code.business.dao.ProducerDAO;
import code.business.dao.ProductDAO;
import code.business.dao.PurchaseDAO;
import code.domain.Customer;
import code.domain.Producer;
import code.infrastructure.configuration.ApplicationConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static code.TestData.getTest1Customer;
import static code.TestData.getTest1Producer;

@SpringJUnitConfig(classes = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Testcontainers
public class CRUDTest {

   private final CustomerDAO customerDAO;
   private final ProducerDAO producerDAO;
   private final ProductDAO productDAO;
   private final PurchaseDAO purchaseDAO;
   private final OpinionDAO opinionDAO;
   private final SimpleDriverDataSource simpleDriverDataSource;

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");
   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jdbc.user", postgreSQL::getUsername);
      registry.add("jdbc.pass", postgreSQL::getPassword);
   }

   @Test
   void testCustomer() {
      // Create
      Customer test1 = getTest1Customer();
      Integer key = customerDAO.add(test1);

      // Read
      Optional<Customer> read = customerDAO.get(key);
      Assertions.assertTrue(read.isPresent());
      Customer test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getUserName(), test1Read.getUserName());
      // SecondRead
      Optional<Customer> read2 = customerDAO.get(key);
      Assertions.assertTrue(read2.isPresent());
      Customer test1Read2 = read2.orElseThrow();
      Assertions.assertSame(read.orElseThrow(), read2.orElseThrow());


      // Update
      String[] params = test1Read.getParams();
      params[0] = "newUserName";
      Customer updatedTest1 = customerDAO.update(key, params);
      Optional<Customer> updatedRead = customerDAO.get(key);
      Assertions.assertTrue(updatedRead.isPresent());
      Customer updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getUserName(), "newUserName");
      Assertions.assertEquals(updatedTest1Read.getUserName(), "newUserName");

      //Delete
      customerDAO.delete(key);
      Assertions.assertTrue(customerDAO.get(key).isEmpty());
   }

   @Test
   void testProducer() {
      // Create
      Producer test1 = getTest1Producer();
      Integer key = producerDAO.add(test1);

      // Read
      Optional<Producer> read = producerDAO.get(key);
      Assertions.assertTrue(read.isPresent());
      Producer test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getAddress(), test1Read.getAddress());
      // SecondRead
      Optional<Producer> read2 = producerDAO.get(key);
      Assertions.assertTrue(read2.isPresent());
      Producer test1Read2 = read2.orElseThrow();
      Assertions.assertSame(read.orElseThrow(), read2.orElseThrow());

      // Update
      String[] params = test1Read.getParams();
      params[1] = "newAddress";
      Producer updatedTest1 = producerDAO.update(key, params);
      Optional<Producer> updatedRead = producerDAO.get(key);
      Assertions.assertTrue(updatedRead.isPresent());
      Producer updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getAddress(), "newAddress");
      Assertions.assertEquals(updatedTest1Read.getAddress(), "newAddress");

      //Delete
      producerDAO.delete(key);
      Assertions.assertTrue(producerDAO.get(key).isEmpty());

   }

   @Test
   void testProduct() {
      // Create
      Producer test1 = getTest1Producer();
      Integer key = producerDAO.add(test1);

      // Read
      Optional<Producer> read = producerDAO.get(key);
      Assertions.assertTrue(read.isPresent());
      Producer test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getAddress(), test1Read.getAddress());

      // Update
      String[] params = test1Read.getParams();
      params[1] = "newAddress";
      Producer updatedTest1 = producerDAO.update(key, params);
      Optional<Producer> updatedRead = producerDAO.get(key);
      Assertions.assertTrue(updatedRead.isPresent());
      Producer updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getAddress(), "newAddress");
      Assertions.assertEquals(updatedTest1Read.getAddress(), "newAddress");

      //Delete
      producerDAO.delete(key);
      Assertions.assertThrows(RuntimeException.class, () -> producerDAO.get(key));
   }
}