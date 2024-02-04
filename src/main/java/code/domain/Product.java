package code.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;

@Value
@ToString(exclude = {"producer"})
@EqualsAndHashCode(exclude = {"producer"})
@Builder
@With
public class Product {
   Integer id;
   String code;
   String name;
   BigDecimal price;
   boolean adultsOnly;
   String description;
   Producer producer;

   public String[] getParams() {
      return new String[] {
              code,
              name,
              price.toString(),
              "" + adultsOnly,
              description,
              producer.getId().toString()
      };
   }
}