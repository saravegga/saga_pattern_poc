package com.vegga.airline.validator;

import com.vegga.airline.dto.BookingInput;

import java.util.ArrayList;
import java.util.List;

public class BookingValidator {

  public static List<String> validate(BookingInput input) {

    List<String> errors = new ArrayList<>();

    if (input.getFlightId() == 34L) { // random invalid number just for the sake of validation
      errors.add("This flightId does not exist.");
    }

    return errors;
  }
}
