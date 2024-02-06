package code.business.service;

import code.TestData;
import code.domain.Customer;
import code.domain.Opinion;
import code.domain.Product;
import code.domain.Purchase;
import code.infrastructure.configuration.TestApplicationConfiguration;
import code.infrastructure.database.repository.CustomerRepository;
import code.infrastructure.database.repository.OpinionRepository;
import code.infrastructure.database.repository.ProducerRepository;
import code.infrastructure.database.repository.ProductRepository;
import code.infrastructure.database.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

@SpringJUnitConfig(value = {TestApplicationConfiguration.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Testcontainers
class ServiceTest {

   private CustomerService customerService;
   private OpinionService opinionService;
   private ProducerService producerService;
   private ProductService productService;
   private PurchaseService purchaseService;
   private DatabaseService databaseService;
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

   @BeforeEach
   void cleanDatabase() {
      databaseService.deleteAll();
   }

   @Test
   void customerServiceTest() {
      customerRepository.add(TestData.getTest1Customer());
      customerService.deleteCustomersWhereAgeBelow16();
      List<Customer> whereAgeBelow = customerRepository.getWhereAgeBelow(16);
      Assertions.assertTrue(whereAgeBelow.isEmpty());
   }

   @Test
   @DisplayName("Assert NotValid Deletion")
   void opinionServiceValidationTest1() {
      Integer id = opinionRepository.add(TestData.getTest1Opinion());
      Assertions.assertTrue(opinionRepository.getValidOpinions().isEmpty());
      opinionService.validateThatOpinionMatchesPurchase();
      Assertions.assertTrue(opinionRepository.getValidOpinions().isEmpty());
      Assertions.assertTrue(opinionRepository.getById(id).isEmpty());
   }

   @Test
   @DisplayName("Assert Valid is not deleted")
   void opinionServiceValidationTest2() {
      Integer id = opinionRepository.add(TestData.getTest1Opinion());
      Opinion opinion = opinionRepository.getById(id).orElseThrow();
      purchaseService.buyProduct(opinion.getCustomer().getId(), opinion.getProduct().getId(), 1);
      Assertions.assertFalse(opinionRepository.getValidOpinions().isEmpty());
      opinionService.validateThatOpinionMatchesPurchase();
      Assertions.assertFalse(opinionRepository.getValidOpinions().isEmpty());
      Assertions.assertTrue(opinionRepository.getById(id).isPresent());
   }

   @Test
   void OpinionServiceStarAdjustTest() {
      opinionRepository.add(TestData.getTest1Opinion());
      List<Opinion> whereLowStars = opinionRepository.getWhereLowStars();
      Assertions.assertFalse(whereLowStars.isEmpty());
      opinionService.adjustQuestionableOpinions();
      whereLowStars = opinionRepository.getWhereLowStars();
      Assertions.assertTrue(whereLowStars.isEmpty());
   }

   @Test
   void productServiceTest() {
      productService.deleteQuestionableProducts();
      List<Product> questionableProducts = productRepository.getQuestionableProducts();
      Assertions.assertTrue(questionableProducts.isEmpty());
   }

   @Test
   void purchaseServiceTest() {
      Integer customerId = customerRepository.add(TestData.getTest1Customer());
      Integer productId = productRepository.add(TestData.getTest1Product());
      Integer id = purchaseService.buyProduct(customerId, productId, 1);
      Optional<Purchase> byId = purchaseRepository.getById(id);
      Assertions.assertTrue(byId.isPresent());
   }

}