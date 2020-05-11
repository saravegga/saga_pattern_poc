package com.vegga.airline.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "airline_reservation")
@Data
public class AirlineReservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "flight_id")
  private Long flightId;

  @Column(name = "client_id")
  private Long clientId;

  @Column(name = "seat_id")
  private Long seatId;
}
