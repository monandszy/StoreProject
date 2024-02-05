package code.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;

@Value
@ToString(exclude = {"customer", "product"})
@EqualsAndHashCode(exclude = {"customer", "product"})
@Builder
@With
public class Purchase {
   Integer id;
   Customer customer;
   Product product;
   int quantity;
   OffsetDateTime timeOfPurchase;

   public String[] getParams() {
      return new String[]{
              "" + customer.getId(),
              "" + product.getId(),
              "" + quantity,
              timeOfPurchase.toString()
      };
   }
}