package code;

import code.domain.Customer;
import code.domain.Producer;
import code.domain.Product;
import code.domain.Purchase;
import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.database.repository.CustomerRepository;
import code.infrastructure.database.repository.OpinionRepository;
import code.infrastructure.database.repository.ProducerRepository;
import code.infrastructure.database.repository.ProductRepository;
import code.infrastructure.database.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static code.TestData.getTest1Customer;
import static code.TestData.getTest1Producer;
import static code.TestData.getTest1Product;
import static code.TestData.getTest1Purchase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Testcontainers
public class CRUDTest {

   private CustomerRepository customerRepository;
   private ProducerRepository producerRepository;
   private ProductRepository productRepository;
   private PurchaseRepository purchaseRepository;
   private OpinionRepository opinionRepository;

   @Container
   static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>("postgres:16.1");
   @DynamicPropertySource
   static void postgreSQLProperties(DynamicPropertyRegistry registry) {
      registry.add("jdbc.url", postgreSQL::getJdbcUrl);
      registry.add("jdbc.user", postgreSQL::getUsername);
      registry.add("jdbc.pass", postgreSQL::getPassword);
   }

   @Test
   @Order(1)
   void testCustomer() {
      // Create
      Customer test1 = getTest1Customer();
      Integer id = customerRepository.add(test1);

      // Read
      Optional<Customer> read = customerRepository.get(id);
      Assertions.assertTrue(read.isPresent());
      Customer test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getParams(), test1Read.getParams());
      // SecondRead
      Optional<Customer> read2 = customerRepository.get(id);
      Assertions.assertTrue(read2.isPresent());
      Customer test1Read2 = read2.orElseThrow();
      Assertions.assertSame(test1Read, test1Read2);


      // Update
      String[] params = test1Read.getParams();
      params[0] = "newUserName";
      Customer updatedTest1 = customerRepository.update(id, params);
      Optional<Customer> updatedRead = customerRepository.get(id);
      Assertions.assertTrue(updatedRead.isPresent());
      Customer updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getUserName(), "newUserName");
      Assertions.assertEquals(updatedTest1Read.getUserName(), "newUserName");

      //Delete
      customerRepository.delete(id);
      Assertions.assertTrue(customerRepository.get(id).isEmpty());
   }

   @Test
   @Order(2)
   void testProducer() {
      // Create
      Producer test1 = getTest1Producer();
      Integer id = producerRepository.add(test1);

      // Read
      Optional<Producer> read = producerRepository.get(id);
      Assertions.assertTrue(read.isPresent());
      Producer test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getParams(), test1Read.getParams());
      // SecondRead
      Optional<Producer> read2 = producerRepository.get(id);
      Assertions.assertTrue(read2.isPresent());
      Producer test1Read2 = read2.orElseThrow();
      Assertions.assertSame(test1Read, test1Read2);

      // Update
      String[] params = test1Read.getParams();
      params[1] = "newAddress";
      Producer updatedTest1 = producerRepository.update(id, params);
      Optional<Producer> updatedRead = producerRepository.get(id);
      Assertions.assertTrue(updatedRead.isPresent());
      Producer updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getAddress(), "newAddress");
      Assertions.assertEquals(updatedTest1Read.getAddress(), "newAddress");

      //Delete
      producerRepository.delete(id);
      Assertions.assertTrue(producerRepository.get(id).isEmpty());

   }


   @Test
   @Order(3)
   void testProduct() {
      this.producerRepository = Mockito.mock();
      when(producerRepository.add(any(Producer.class))).thenReturn(1);
      when(producerRepository.get(anyInt())).thenReturn(Optional.of(getTest1Producer()));

      // Create
      Product test1 = getTest1Product();
      Integer id = productRepository.add(test1);

      // Read
      Optional<Product> read = productRepository.get(id);
      Assertions.assertTrue(read.isPresent());
      Product test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getCode(), test1Read.getCode());
      // SecondRead
      Optional<Product> read2 = productRepository.get(id);
      Assertions.assertTrue(read2.isPresent());
      Product test1Read2 = read2.orElseThrow();
      Assertions.assertSame(test1Read, test1Read2);

      // Update
      String[] params = test1Read.getParams();
      params[0] = "newCode";
      Product updatedTest1 = productRepository.update(id, params);
      Optional<Product> updatedRead = productRepository.get(id);
      Assertions.assertTrue(updatedRead.isPresent());
      Product updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getCode(), "newCode");
      Assertions.assertEquals(updatedTest1Read.getCode(), "newCode");

      //Delete
      productRepository.delete(id);
      Assertions.assertTrue(productRepository.get(id).isEmpty());
   }

   @Test
   @Order(4)
   void testPurchase() {
      this.customerRepository = Mockito.mock();
      when(customerRepository.add(any(Customer.class))).thenReturn(null);
      when(customerRepository.get(anyInt())).thenReturn(Optional.of(getTest1Customer()));

      this.productRepository = Mockito.mock();
      when(productRepository.add(any(Product.class))).thenReturn(null);
      when(productRepository.get(anyInt())).thenReturn(Optional.of(getTest1Product()));

      // Create
      Purchase test1 = getTest1Purchase();
      Integer id = purchaseRepository.add(test1);

      // Read
      Optional<Purchase> read = purchaseRepository.get(id);
      Assertions.assertTrue(read.isPresent());
      Purchase test1Read = read.orElseThrow();
      Assertions.assertEquals(test1.getQuantity(), test1Read.getQuantity());
      // SecondRead
      Optional<Purchase> read2 = purchaseRepository.get(id);
      Assertions.assertTrue(read2.isPresent());
      Purchase test1Read2 = read2.orElseThrow();
      Assertions.assertSame(test1Read, test1Read2);

      // Update
      String[] params = test1Read.getParams();
      params[2] = "2";
      Purchase updatedTest1 = purchaseRepository.update(id, params);
      Optional<Purchase> updatedRead = purchaseRepository.get(id);
      Assertions.assertTrue(updatedRead.isPresent());
      Purchase updatedTest1Read = updatedRead.orElseThrow();
      Assertions.assertEquals(updatedTest1.getQuantity(), 2);
      Assertions.assertEquals(updatedTest1Read.getQuantity(), 2);

      //Delete
      purchaseRepository.delete(id);
      Assertions.assertTrue(purchaseRepository.get(id).isEmpty());
   }
}