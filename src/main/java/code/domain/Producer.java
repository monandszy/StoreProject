package code.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Producer {
   Integer id;
   String name;
   String address;


   public String[] getParams() {
      return new String[]{
              name,
              address
      };
   }
}