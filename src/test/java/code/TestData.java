package code;

import code.domain.Customer;
import code.domain.Opinion;
import code.domain.Producer;
import code.domain.Product;
import code.domain.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class TestData {
   public static Producer getTest1Producer() {
      Producer test1 = Producer.builder()
              .name("test1name")
              .address("test1address")
              .build();
      return test1;
   }

   public static Customer getTest1Customer() {
      Customer test1 = Customer.builder()
              .userName("test1userName")
              .email("test1email")
              .name("test1name")
              .surname("test1surname")
              .dateOfBirth(LocalDate.now())
              .build();
      return test1;
   }

   public static Product getTest1Product() {
      Product test1 = Product.builder()
              .name("test1name")
              .price(new BigDecimal("100.1"))
              .code("test1code")
              .adultsOnly(false)
              .description("test1description")
              .producer(getTest1Producer())
              .build();
      return test1;
   }

   public static Purchase getTest1Purchase() {
      Purchase test1 = Purchase.builder()
              .customer(getTest1Customer())
              .product(getTest1Product())
              .quantity(1)
              .timeOfPurchase(ZonedDateTime.now())
              .build();
      return test1;
   }

   public static Opinion getTest1Opinion() {
      Opinion test1 = Opinion.builder()
              .customer(getTest1Customer())
              .product(getTest1Product())
              .stars(1)
              .comment("test1comment")
              .timeOfComment(ZonedDateTime.now())
              .build();
      return test1;
   }
}