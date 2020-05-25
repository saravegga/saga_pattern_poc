package com.vegga.trip.dto;

import lombok.Data;

@Data
public class TripInput {

  private AirlineBookingInput airlineBooking;
  private CarBookingInput carBooking;
  private HotelBookingInput hotelBooking;
  private PaymentBookingInput payment;
}
