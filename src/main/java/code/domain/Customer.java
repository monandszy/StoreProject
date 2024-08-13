package code.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;

@Value
@Builder
@With
public class Customer {
  Integer id;
  String userName;
  String email;
  String name;
  String surname;
  LocalDate dateOfBirth;

  public String[] getParams() {
    return new String[]{
      userName,
      email,
      name,
      surname,
      dateOfBirth.toString()
    };
  }
}