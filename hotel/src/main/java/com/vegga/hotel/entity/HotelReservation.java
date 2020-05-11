package com.vegga.hotel.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_reservation")
@Data
public class HotelReservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hotel_id")
  private Long hotelId;

  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "client_id")
  private Long clientId;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;
}
