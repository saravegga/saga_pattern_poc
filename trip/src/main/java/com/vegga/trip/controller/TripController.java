package com.vegga.trip.controller;

import com.vegga.trip.dto.MessageOutput;
import com.vegga.trip.dto.PaymentBookingInput;
import com.vegga.trip.dto.TripInput;
import com.vegga.trip.queue.AirlineBookingRPCClient;
import com.vegga.trip.queue.CarBookingRPCClient;
import com.vegga.trip.queue.HotelBookingRPCClient;
import com.vegga.trip.queue.PaymentRPCClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripController {

  @Autowired AirlineBookingRPCClient airlineBookingRPCClient;

  @Autowired CarBookingRPCClient carBookingRPCClient;

  @Autowired HotelBookingRPCClient hotelBookingRPCClient;

  @Autowired PaymentRPCClient paymentRPCClient;

  @GetMapping(value = "/check")
  public String check() {
    return "Trip service is alive";
  }

  @PostMapping
  public ResponseEntity createBooking(@RequestBody TripInput input) throws Exception {
    MessageOutput validateAirline =
        airlineBookingRPCClient.validateInput(input.getAirlineBooking());
    MessageOutput validateCar = carBookingRPCClient.validateInput(input.getCarBooking());
    MessageOutput validateHotel = hotelBookingRPCClient.validateInput(input.getHotelBooking());
    MessageOutput validatePayment = paymentRPCClient.validateInput(input.getPayment());

    if (hasError(validateAirline)
        || hasError(validateCar)
        || hasError(validateHotel)
        || hasError(validatePayment)) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    MessageOutput saveAirline = airlineBookingRPCClient.save(input.getAirlineBooking());
    if (hasError(saveAirline)) {
      // error
    }

    MessageOutput saveCar = carBookingRPCClient.save(input.getCarBooking());
    if (hasError(saveCar)) {
      // rollback airline
      // error
    }

    MessageOutput saveHotel = hotelBookingRPCClient.save(input.getHotelBooking());
    if (hasError(saveHotel)) {
      // rollback airline
      // rollback car
      // error
    }

    includeIds(
        input.getPayment(),
        saveAirline.getObjectId(),
        saveCar.getObjectId(),
        saveHotel.getObjectId());
    MessageOutput savePayment = paymentRPCClient.save(input.getPayment());
    if (hasError(savePayment)) {
      // rollback airline
      // rollback car
      // rollback hotel
      // error
    }

    return new ResponseEntity(HttpStatus.OK);
  }

  private void includeIds(
      PaymentBookingInput paymentInput,
      Long airlineBookingId,
      Long carBookingId,
      Long hotelBookingId) {
    paymentInput.setAirlineReservationId(airlineBookingId);
    paymentInput.setCarReservationId(carBookingId);
    paymentInput.setHotelReservationId(hotelBookingId);
  }

  private boolean hasError(MessageOutput messageOutput) {
    return messageOutput == null || !CollectionUtils.isEmpty(messageOutput.getErrors());
  }
}
