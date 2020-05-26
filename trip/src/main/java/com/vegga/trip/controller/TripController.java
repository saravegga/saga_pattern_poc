package com.vegga.trip.controller;

import com.vegga.trip.dto.*;
import com.vegga.trip.queue.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class TripController {

  @Autowired AirlineBookingRPCClient airlineBookingRPCClient;

  @Autowired CarBookingRPCClient carBookingRPCClient;

  @Autowired HotelBookingRPCClient hotelBookingRPCClient;

  @Autowired PaymentRPCClient paymentRPCClient;

  @Autowired EventstoreClient eventstoreClient;

  @GetMapping(value = "/check")
  public String check() {
    return "Trip service is alive";
  }

  @PostMapping
  public ResponseEntity createBooking(@RequestBody TripInput input) throws Exception {

    // Current transactionalId
    UUID transactionalId = UUID.randomUUID();

    // Converting the inputs into BaseDTO<T>
    BaseDTO<AirlineBookingInput> airlineInput =
        AirlineBookingInput.convert(input.getAirlineBooking(), transactionalId);
    BaseDTO<CarBookingInput> carInput =
        CarBookingInput.convert(input.getCarBooking(), transactionalId);
    BaseDTO<HotelBookingInput> hotelInput =
        HotelBookingInput.convert(input.getHotelBooking(), transactionalId);
    BaseDTO<PaymentBookingInput> paymentInput =
        PaymentBookingInput.convert(input.getPayment(), transactionalId);

    // Validations
    MessageOutput validateAirline = airlineBookingRPCClient.validateInput(airlineInput);
    MessageOutput validateCar = carBookingRPCClient.validateInput(carInput);
    MessageOutput validateHotel = hotelBookingRPCClient.validateInput(hotelInput);
    MessageOutput validatePayment = paymentRPCClient.validateInput(paymentInput);

    if (hasError(validateAirline)
        || hasError(validateCar)
        || hasError(validateHotel)
        || hasError(validatePayment)) {
      return new ResponseEntity(
          appendErrors(validateAirline, validateCar, validateHotel, validatePayment),
          HttpStatus.BAD_REQUEST);
    }

    // Saving the values
    MessageOutput saveAirline = airlineBookingRPCClient.save(airlineInput);
    if (hasError(saveAirline)) {
      // Error
      return new ResponseEntity(saveAirline, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    MessageOutput saveCar = carBookingRPCClient.save(carInput);
    if (hasError(saveCar)) {
      // Error + Compensating transactions
      BaseDTO<AirlineBookingInput> airlineBefore = eventstoreClient.getEventLog(airlineInput);
      airlineBookingRPCClient.abort(
          AirlineBookingInput.createAbortMessage(
              airlineBefore, airlineInput, saveAirline.getObjectId()));
      return new ResponseEntity(saveCar, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    MessageOutput saveHotel = hotelBookingRPCClient.save(hotelInput);
    if (hasError(saveHotel)) {
      // Error + Compensating transactions
      BaseDTO<AirlineBookingInput> airlineBefore = eventstoreClient.getEventLog(airlineInput);
      airlineBookingRPCClient.abort(
          AirlineBookingInput.createAbortMessage(
              airlineBefore, airlineInput, saveAirline.getObjectId()));
      BaseDTO<CarBookingInput> carBefore = eventstoreClient.getEventLog(carInput);
      carBookingRPCClient.abort(
          CarBookingInput.createAbortMessage(carBefore, carInput, saveCar.getObjectId()));
      return new ResponseEntity(saveHotel, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    includeIdsIntoPayment(
        input.getPayment(),
        saveAirline.getObjectId(),
        saveCar.getObjectId(),
        saveHotel.getObjectId());
    MessageOutput savePayment = paymentRPCClient.save(paymentInput);
    if (hasError(savePayment)) {
      // Error + Compensating transactions
      BaseDTO<AirlineBookingInput> airlineBefore = eventstoreClient.getEventLog(airlineInput);
      airlineBookingRPCClient.abort(
          AirlineBookingInput.createAbortMessage(
              airlineBefore, airlineInput, saveAirline.getObjectId()));
      BaseDTO<CarBookingInput> carBefore = eventstoreClient.getEventLog(carInput);
      carBookingRPCClient.abort(
          CarBookingInput.createAbortMessage(carBefore, carInput, saveCar.getObjectId()));
      BaseDTO<HotelBookingInput> hotelBefore = eventstoreClient.getEventLog(hotelInput);
      hotelBookingRPCClient.abort(
          HotelBookingInput.createAbortMessage(hotelBefore, hotelInput, saveHotel.getObjectId()));
      return new ResponseEntity(savePayment, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Changing eventstore status to finished and saving the correct ids
    correctIds(
        airlineInput,
        carInput,
        hotelInput,
        paymentInput,
        saveAirline,
        saveCar,
        saveHotel,
        savePayment);
    eventstoreClient.finishEventStatus(airlineInput);
    eventstoreClient.finishEventStatus(carInput);
    eventstoreClient.finishEventStatus(hotelInput);
    eventstoreClient.finishEventStatus(paymentInput);
    return new ResponseEntity(HttpStatus.OK);
  }

  private void correctIds(
      BaseDTO<AirlineBookingInput> airlineInput,
      BaseDTO<CarBookingInput> carInput,
      BaseDTO<HotelBookingInput> hotelInput,
      BaseDTO<PaymentBookingInput> paymentInput,
      MessageOutput saveAirline,
      MessageOutput saveCar,
      MessageOutput saveHotel,
      MessageOutput savePayment) {

    airlineInput.setObjectId(saveAirline.getObjectId());
    carInput.setObjectId(saveCar.getObjectId());
    hotelInput.setObjectId(saveHotel.getObjectId());
    paymentInput.setObjectId(savePayment.getObjectId());

    airlineInput.getEntity().setId(saveAirline.getObjectId());
    carInput.getEntity().setId(saveCar.getObjectId());
    hotelInput.getEntity().setId(saveHotel.getObjectId());
    paymentInput.getEntity().setId(savePayment.getObjectId());
  }

  private Object appendErrors(
      MessageOutput validateAirline,
      MessageOutput validateCar,
      MessageOutput validateHotel,
      MessageOutput validatePayment) {
    List<String> errors = new ArrayList<>();
    errors.addAll(validateAirline.getErrors());
    errors.addAll(validateCar.getErrors());
    errors.addAll(validateHotel.getErrors());
    errors.addAll(validatePayment.getErrors());
    return new MessageOutput(validateAirline.getTransactionalId(), null, errors);
  }

  private void includeIdsIntoPayment(
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
