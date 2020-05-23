package com.vegga.trip.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AirlineBookingInput {

  private Long id;
  private Long flightId;
  private Long clientId;
  private Long seatId;
  private UUID transactionalId;
}
