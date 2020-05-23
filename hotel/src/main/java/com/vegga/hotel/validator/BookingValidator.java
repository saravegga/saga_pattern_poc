package com.vegga.hotel.validator;

import com.vegga.hotel.dto.BookingInput;

import java.util.ArrayList;
import java.util.List;

public class BookingValidator {

  public static List<String> validate(BookingInput input) {

    List<String> errors = new ArrayList<>();

    if (input.getHotelId() == 34L) { // random invalid number just for the sake of validation
      errors.add("This hotelId does not exist.");
    }

    return errors;
  }
}
