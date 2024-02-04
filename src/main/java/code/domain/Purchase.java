package code.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.time.ZonedDateTime;

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
   ZonedDateTime timeOfPurchase;

   public String[] getParams() {
      return new String[] {
              customer.getId().toString(),
              product.getId().toString(),
              "" + quantity,
              timeOfPurchase.toString()
      };
   }
}