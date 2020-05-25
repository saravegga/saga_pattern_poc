package com.vegga.airline.dto;

import lombok.Data;

@Data
public class BookingInput {

  private Long id;
  private Long flightId;
  private Long clientId;
  private Long seatId;
}
