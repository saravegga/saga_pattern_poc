package com.vegga.car.validator;

import com.vegga.car.dto.BookingInput;

public class BookingValidator {

  public static void validate(BookingInput input) {
    if (input.getCarId() == 34L) { // random invalid number just for the sake of validation
      throw new IllegalArgumentException("This carId does not exist.");
    }
  }
}
