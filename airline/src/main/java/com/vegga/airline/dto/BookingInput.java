package com.vegga.airline.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BookingInput {

  private Long id;
  private Long flightId;
  private Long clientId;
  private Long seatId;
  private UUID transactionalId;
}
