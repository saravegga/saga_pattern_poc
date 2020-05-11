package com.vegga.payment.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "car_reservation_id")
  private Long carReservationId;

  @Column(name = "airline_reservation_id")
  private Long airlineReservationId;

  @Column(name = "hotel_reservation_id")
  private Long hotelReservationId;

  @Column(name = "client_id")
  private Long clientId;

  @Column(name = "payment_date")
  private LocalDateTime paymentDate;

  @Column(name = "payment_expiry_date")
  private LocalDateTime paymentExpiryDate;

  @Column(name = "creation_date")
  private LocalDateTime creationDate;
}
