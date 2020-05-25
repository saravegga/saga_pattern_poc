package com.vegga.payment.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class BookingInput {

  private Long id;
  private Long carReservationId;
  private Long airlineReservationId;
  private Long hotelReservationId;
  private Long clientId;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime paymentDate;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime paymentExpiryDate;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime creationDate;
}
