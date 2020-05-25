package com.vegga.trip.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AirlineBookingInput {

  private Long id;
  private Long flightId;
  private Long clientId;
  private Long seatId;

  public static BaseDTO<AirlineBookingInput> convert(
      AirlineBookingInput input, UUID transactionalId) {
    return new BaseDTO<>(
        transactionalId, input.getId(), AirlineBookingInput.class.getSimpleName(), input);
  }

  public static AbortDTO<AirlineBookingInput> createAbortMessage(
      BaseDTO<AirlineBookingInput> before, BaseDTO<AirlineBookingInput> then, Long id) {
    then.setObjectId(id);
    then.getEntity().setId(id);
    return new AbortDTO<>(before, then);
  }
}
