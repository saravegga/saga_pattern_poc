package com.vegga.payment.validator;

import com.vegga.payment.dto.BookingInput;

import java.util.ArrayList;
import java.util.List;

public class BookingValidator {

  public static List<String> validate(BookingInput input) {

    List<String> errors = new ArrayList<>();

    if (input.getAirlineReservationId()
        == 34L) { // random invalid number just for the sake of validation
      errors.add("This airlineReservationId does not exist.");
    }

    return errors;
  }
}
