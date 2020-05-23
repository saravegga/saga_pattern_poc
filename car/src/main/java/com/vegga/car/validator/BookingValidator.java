package com.vegga.car.validator;

import com.vegga.car.dto.BookingInput;

import java.util.ArrayList;
import java.util.List;

public class BookingValidator {

  public static List<String> validate(BookingInput input) {

    List<String> errors = new ArrayList<>();

    if (input.getCarId() == 34L) { // random invalid number just for the sake of validation
      errors.add("This carId does not exist.");
    }

    return errors;
  }
}
