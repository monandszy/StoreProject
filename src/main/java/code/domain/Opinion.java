package code.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Value
@ToString(exclude = {"customer", "product"})
@EqualsAndHashCode(exclude = {"customer", "product"})
@Builder
@With
public class Opinion {
   Integer id;
   Customer customer;
   Product product;
   int stars;
   String comment;
   OffsetDateTime timeOfComment;

   public String[] getParams() {
      return new String[] {
              "" + customer.getId(),
              "" + product.getId(),
              "" + stars,
              comment,
              timeOfComment.toString()
      };
   }
}